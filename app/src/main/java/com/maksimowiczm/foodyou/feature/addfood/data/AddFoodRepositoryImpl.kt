package com.maksimowiczm.foodyou.feature.addfood.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.QuantitySuggestion.Companion.defaultSuggestion
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.feature.addfood.data.model.toDomain
import com.maksimowiczm.foodyou.feature.addfood.data.model.toEntity
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDao
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.addfood.database.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.feature.product.data.ProductPreferences
import com.maksimowiczm.foodyou.feature.product.data.model.toDomain
import com.maksimowiczm.foodyou.feature.product.data.model.toEntity
import com.maksimowiczm.foodyou.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.feature.product.network.openfoodfacts.OpenFoodFactsNetworkDataSource
import com.maksimowiczm.foodyou.infrastructure.database.TransactionProvider
import com.maksimowiczm.foodyou.infrastructure.datastore.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class AddFoodRepositoryImpl(
    addFoodDatabase: AddFoodDatabase,
    productDatabase: ProductDatabase,
    private val transactionProvider: TransactionProvider,
    private val dataStore: DataStore<Preferences>,
    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource
) : AddFoodRepository {
    private val addFoodDao: AddFoodDao = addFoodDatabase.addFoodDao()
    private val productDao: ProductDao = productDatabase.productDao()

    override suspend fun addFood(
        date: LocalDate,
        meal: Meal,
        productId: Long,
        weightMeasurement: WeightMeasurement
    ): Long {
        val quantity = when (weightMeasurement) {
            is WeightMeasurement.WeightUnit -> weightMeasurement.weight
            is WeightMeasurement.Package -> weightMeasurement.quantity
            is WeightMeasurement.Serving -> weightMeasurement.quantity
        }

        return addFood(
            date = date,
            meal = meal,
            productId = productId,
            weightMeasurement = weightMeasurement.asEnum(),
            quantity = quantity
        )
    }

    override suspend fun addFood(
        date: LocalDate,
        meal: Meal,
        productId: Long,
        weightMeasurement: WeightMeasurementEnum,
        quantity: Float
    ): Long {
        val localZoneOffset = ZoneOffset.systemDefault()
        val currentLocalTime = LocalDateTime.now().atZone(localZoneOffset)
        val epochSeconds = currentLocalTime.toEpochSecond()

        val entity = WeightMeasurementEntity(
            mealId = meal.toEntity(),
            diaryEpochDay = date.toEpochDay(),
            productId = productId,
            measurement = weightMeasurement,
            quantity = quantity,
            createdAt = epochSeconds
        )

        return addFoodDao.insertWeightMeasurement(entity)
    }

    override suspend fun removeFood(portionId: Long) {
        val entity = addFoodDao.observeWeightMeasurement(portionId).first()

        if (entity != null) {
            addFoodDao.deleteWeightMeasurement(entity.id)
        }
    }

    override fun queryProducts(
        meal: Meal,
        date: LocalDate,
        query: String?
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> {
        return if (query?.all { it.isDigit() } == true) {
            queryProductsByBarcode(meal, date, query)
        } else {
            queryProductsByName(meal, date, query)
        }
    }

    private fun queryProductsByBarcode(
        meal: Meal,
        date: LocalDate,
        barcode: String
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> = flow {
        emit(QueryResult.loading(emptyList()))

        val products = addFoodDao.getProductsWithMeasurementByBarcode(
            meal = meal,
            date = date,
            barcode = barcode
        )

        // If OpenFoodFacts is disabled, return local products only
        if (dataStore.get(ProductPreferences.openFoodFactsEnabled) == false) {
            return@flow emit(QueryResult.success(products))
        }

        emit(QueryResult.loading(products))

        // Otherwise, query OpenFoodFacts
        val country = dataStore.get(ProductPreferences.openFoodCountryCode)
        if (country == null) {
            Log.e(TAG, "OpenFoodFacts country code is not set")

            return@flow emit(
                QueryResult.error(
                    error = Error("OpenFoodFacts country code is not set"),
                    data = products
                )
            )
        }

        try {
            val openFoodProduct = openFoodFactsNetworkDataSource.getProduct(
                code = barcode,
                country = country
            )

            if (openFoodProduct == null) {
                Log.d(TAG, "OpenFoodFacts product not found for barcode: $barcode")
                return@flow emit(QueryResult.success(products))
            }

            val product = openFoodProduct.toEntity() ?: run {
                Log.w(
                    TAG,
                    "Failed to convert product: (name=${openFoodProduct.productName}, code=${openFoodProduct.code})"
                )

                return@flow emit(QueryResult.success(products))
            }

            val productsWithMeasurement = transactionProvider.withTransaction {
                productDao.insertOpenFoodFactsProducts(listOf(product))
                addFoodDao.getProductsWithMeasurementByBarcode(
                    meal = meal,
                    date = date,
                    barcode = barcode
                )
            }

            emit(QueryResult.success(productsWithMeasurement))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to query products", e)

            emit(
                QueryResult.error(
                    error = e,
                    data = products
                )
            )
        }
    }

    private fun queryProductsByName(
        meal: Meal,
        date: LocalDate,
        query: String?
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> = flow {
        if (query != null) {
            insertProductQueryWithCurrentTime(query)
        }

        emit(QueryResult.loading(emptyList()))

        val products = addFoodDao.getProductsWithMeasurementByQuery(
            meal = meal,
            date = date,
            query = query
        )

        // If OpenFoodFacts is disabled or query is null, return local products only
        if (dataStore.get(ProductPreferences.openFoodFactsEnabled) == false || query == null) {
            return@flow emit(QueryResult.success(products))
        }

        emit(QueryResult.loading(products))

        // Otherwise, query OpenFoodFacts
        val country = dataStore.get(ProductPreferences.openFoodCountryCode)
        if (country == null) {
            Log.e(TAG, "OpenFoodFacts country code is not set")

            return@flow emit(
                QueryResult.error(
                    error = Error("OpenFoodFacts country code is not set"),
                    data = products
                )
            )
        }

        try {
            val openFoodResponse = openFoodFactsNetworkDataSource.queryProducts(
                query = query,
                country = country,
                page = 1,
                pageSize = PAGE_SIZE
            )

            val openFoodProducts = openFoodResponse.products.mapNotNull { product ->
                product.toEntity().also { entity ->
                    if (entity == null) {
                        Log.w(
                            TAG,
                            "Failed to convert product: (name=${product.productName}, code=${product.code})"
                        )
                    }
                }
            }

            val productsWithMeasurement = transactionProvider.withTransaction {
                productDao.insertOpenFoodFactsProducts(openFoodProducts)
                addFoodDao.getProductsWithMeasurementByQuery(
                    meal = meal,
                    date = date,
                    query = query
                )
            }

            emit(QueryResult.success(productsWithMeasurement))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to query products", e)

            emit(
                QueryResult.error(
                    error = e,
                    data = products
                )
            )
        }
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val zone = ZoneOffset.UTC
        val time = LocalDateTime.now().atZone(zone)
        val epochSeconds = time.toEpochSecond()

        addFoodDao.upsertProductQuery(
            ProductQueryEntity(
                query = query,
                date = epochSeconds
            )
        )
    }

    override fun observeMeasuredProducts(
        meal: Meal,
        date: LocalDate
    ): Flow<List<ProductWithWeightMeasurement>> {
        return addFoodDao.observeMeasuredProducts(
            mealId = meal.toEntity().value,
            epochDay = date.toEpochDay()
        ).map { it.map { entity -> entity.toDomain() } }
    }

    override suspend fun getQuantitySuggestionByProductId(productId: Long): QuantitySuggestion {
        val product = productDao.getProductById(productId) ?: error("Product not found")

        val suggestionList = addFoodDao.observeQuantitySuggestionsByProductId(productId).first()

        val suggestions = suggestionList
            .associate { it.measurement to it.quantity }
            .toMutableMap()

        val default = defaultSuggestion()
        WeightMeasurementEnum.entries.forEach {
            if (!suggestions.containsKey(it)) {
                suggestions[it] = default[it] ?: error("Default suggestion not found for $it")
            }
        }

        return QuantitySuggestion(
            product = product.toDomain(),
            quantitySuggestions = suggestions
        )
    }

    override fun observeProductQueries(limit: Int): Flow<List<ProductQuery>> {
        return addFoodDao.observeLatestQueries(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    private suspend fun AddFoodDao.getProductsWithMeasurementByQuery(
        meal: Meal,
        date: LocalDate,
        query: String?
    ): List<ProductWithWeightMeasurement> = observeProductsWithMeasurement(
        mealId = meal.toEntity().value,
        epochDay = date.toEpochDay(),
        query = query,
        barcode = null,
        limit = PAGE_SIZE
    ).first().map { it.toDomain() }

    private suspend fun AddFoodDao.getProductsWithMeasurementByBarcode(
        meal: Meal,
        date: LocalDate,
        barcode: String
    ): List<ProductWithWeightMeasurement> = observeProductsWithMeasurement(
        mealId = meal.toEntity().value,
        epochDay = date.toEpochDay(),
        query = null,
        barcode = barcode,
        limit = PAGE_SIZE
    ).first().map { it.toDomain() }

    companion object {
        private const val TAG = "AddFoodRepositoryImpl"
        private const val PAGE_SIZE = 30
    }
}
