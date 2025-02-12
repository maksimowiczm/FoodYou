package com.maksimowiczm.foodyou.core.feature.addfood.data

import android.util.Log
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion.Companion.defaultSuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.toDomain
import com.maksimowiczm.foodyou.core.feature.addfood.database.AddFoodDao
import com.maksimowiczm.foodyou.core.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.core.feature.addfood.database.ProductQueryEntity
import com.maksimowiczm.foodyou.core.feature.addfood.database.WeightMeasurementEntity
import com.maksimowiczm.foodyou.core.feature.diary.data.QueryResult
import com.maksimowiczm.foodyou.core.feature.product.data.model.toDomain
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.RemoteProductDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class AddFoodRepositoryImpl(
    addFoodDatabase: AddFoodDatabase,
    productDatabase: ProductDatabase,
    private val remoteProductDatabase: RemoteProductDatabase,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : AddFoodRepository {
    private val addFoodDao: AddFoodDao = addFoodDatabase.addFoodDao()
    private val productDao: ProductDao = productDatabase.productDao()

    override suspend fun addFood(
        date: LocalDate,
        mealId: Long,
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
            mealId = mealId,
            productId = productId,
            weightMeasurement = weightMeasurement.asEnum(),
            quantity = quantity
        )
    }

    override suspend fun addFood(
        date: LocalDate,
        mealId: Long,
        productId: Long,
        weightMeasurement: WeightMeasurementEnum,
        quantity: Float
    ): Long {
        val epochSeconds = Clock.System.now().epochSeconds

        val entity = WeightMeasurementEntity(
            mealId = mealId,
            diaryEpochDay = date.toEpochDays(),
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
        mealId: Long,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> {
        return if (query?.all { it.isDigit() } == true) {
            queryProductsByBarcode(mealId, date, query, localOnly)
        } else {
            queryProductsByName(mealId, date, query, localOnly)
        }
    }

    private fun queryProductsByBarcode(
        mealId: Long,
        date: LocalDate,
        barcode: String,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> = flow {
        val flow = { isLoading: Boolean, error: Throwable? ->
            addFoodDao.observeProductsWithMeasurementByBarcode(
                mealId = mealId,
                date = date,
                barcode = barcode
            ).map { products ->
                QueryResult(
                    data = products,
                    isLoading = isLoading,
                    error = error
                )
            }
        }

        if (!localOnly) {
            // First get local products and emit loading state
            flow(true, null).first().also { emit(it) }

            try {
                remoteProductDatabase.queryAndInsertByBarcode(barcode)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query products", e)

                flow(false, e).collect(::emit)
            }
        }

        flow(false, null).collect(::emit)
    }

    private fun queryProductsByName(
        mealId: Long,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<List<ProductWithWeightMeasurement>>> = flow {
        // Insert the query to the history
        if (query != null) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        val flow = { isLoading: Boolean, error: Throwable? ->
            addFoodDao.observeProductsWithMeasurementByQuery(
                mealId = mealId,
                date = date,
                query = query
            ).map { products ->
                QueryResult(
                    data = products,
                    isLoading = isLoading,
                    error = error
                )
            }
        }

        if (!localOnly) {
            // First get local products and emit loading state
            flow(true, null).first().also { emit(it) }

            try {
                remoteProductDatabase.queryAndInsertByName(query)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query products", e)

                flow(false, e).collect(::emit)
            }
        }

        flow(false, null).collect(::emit)
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

        addFoodDao.upsertProductQuery(
            ProductQueryEntity(
                query = query,
                date = epochSeconds
            )
        )
    }

    override fun observeTotalCalories(mealId: Long, date: LocalDate) =
        addFoodDao.observeMeasuredProducts(
            mealId = mealId,
            epochDay = date.toEpochDays()
        ).map { list ->
            list.sumOf { it.toDomain().calories }
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

    private fun AddFoodDao.observeProductsWithMeasurementByQuery(
        mealId: Long,
        date: LocalDate,
        query: String?
    ): Flow<List<ProductWithWeightMeasurement>> = observeProductsWithMeasurement(
        mealId = mealId,
        epochDay = date.toEpochDays(),
        query = query,
        barcode = null,
        limit = PAGE_SIZE
    ).map { list -> list.map { it.toDomain() } }

    private fun AddFoodDao.observeProductsWithMeasurementByBarcode(
        mealId: Long,
        date: LocalDate,
        barcode: String
    ): Flow<List<ProductWithWeightMeasurement>> = observeProductsWithMeasurement(
        mealId = mealId,
        epochDay = date.toEpochDays(),
        query = null,
        barcode = barcode,
        limit = PAGE_SIZE
    ).map { list -> list.map { it.toDomain() } }

    private companion object {
        private const val TAG = "AddFoodRepositoryImpl"
        private const val PAGE_SIZE = 30
    }
}
