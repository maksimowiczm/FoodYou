package com.maksimowiczm.foodyou.core.feature.addfood.data

import android.util.Log
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductIdWithMeasurementsIds
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
import com.maksimowiczm.foodyou.core.feature.product.data.model.toDomain
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDao
import com.maksimowiczm.foodyou.core.feature.product.database.ProductDatabase
import com.maksimowiczm.foodyou.core.feature.product.network.ProductsRemoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class AddFoodRepositoryImpl(
    addFoodDatabase: AddFoodDatabase,
    productDatabase: ProductDatabase,
    private val productsRemoteDatabase: ProductsRemoteDatabase,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) : AddFoodRepository {
    private val addFoodDao: AddFoodDao = addFoodDatabase.addFoodDao()
    private val productDao: ProductDao = productDatabase.productDao()

    override suspend fun addMeasurement(
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

        val epochSeconds = Clock.System.now().epochSeconds

        val entity = WeightMeasurementEntity(
            mealId = mealId,
            diaryEpochDay = date.toEpochDays(),
            productId = productId,
            measurement = weightMeasurement.asEnum(),
            quantity = quantity,
            createdAt = epochSeconds
        )

        return addFoodDao.insertWeightMeasurement(entity)
    }

    override fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<ProductIdWithMeasurementsIds>> {
        return if (query?.all { it.isDigit() } == true) {
            queryProductsByBarcode(mealId, date, query, localOnly)
        } else {
            queryProductsByQuery(mealId, date, query, localOnly)
        }
    }

    private fun queryProductsByBarcode(
        mealId: Long,
        date: LocalDate,
        barcode: String,
        localOnly: Boolean
    ): Flow<QueryResult<ProductIdWithMeasurementsIds>> = flow {
        val flow = { isLoading: Boolean, error: Throwable? ->
            addFoodDao.observeProductIdsWithMeasurementIds(
                mealId = mealId,
                epochDay = date.toEpochDays(),
                query = null,
                barcode = barcode
            ).map {
                val map = it.groupBy { it.productId }

                map.map { (id, list) ->
                    ProductIdWithMeasurementsIds(
                        productId = id,
                        measurements = list.mapNotNull { it.measurementId }
                    )
                }
            }.map {
                QueryResult(
                    data = it,
                    isLoading = isLoading,
                    error = error
                )
            }
        }

        if (!localOnly) {
            flow(true, null).first().also { emit(it) }

            try {
                productsRemoteDatabase.queryAndInsertByBarcode(
                    barcode = barcode
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query products", e)

                flow(false, e).collect(::emit)
            }
        }

        flow(false, null).collect(::emit)
    }

    private fun queryProductsByQuery(
        mealId: Long,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<QueryResult<ProductIdWithMeasurementsIds>> = flow {
        if (query != null) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        val flow = { isLoading: Boolean, error: Throwable? ->
            addFoodDao.observeProductIdsWithMeasurementIds(
                mealId = mealId,
                query = query,
                epochDay = date.toEpochDays(),
                barcode = null
            ).map { list ->
                val map = list.groupBy { it.productId }

                map.map { (id, list) ->
                    ProductIdWithMeasurementsIds(
                        productId = id,
                        measurements = list.mapNotNull { it.measurementId }
                    )
                }
            }.map {
                QueryResult(
                    data = it,
                    isLoading = isLoading,
                    error = error
                )
            }
        }

        if (!localOnly) {
            flow(true, null).first().also { emit(it) }

            try {
                productsRemoteDatabase.queryAndInsertByName(
                    query = query,
                    limit = NETWORK_PAGE_SIZE
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to query products", e)

                flow(false, e).collect(::emit)
            }
        }

        flow(false, null).collect(::emit)
    }

    override suspend fun removeMeasurement(id: Long) {
        val entity = addFoodDao.observeWeightMeasurement(id).first()

        if (entity != null) {
            addFoodDao.deleteWeightMeasurement(entity.id)
        }
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

    override fun observeQuantitySuggestionByProductId(productId: Long) = combine(
        productDao.observeProductById(productId),
        addFoodDao.observeQuantitySuggestionsByProductId(productId)
    ) { product, suggestionList ->
        if (product == null) {
            Log.w(TAG, "Product not found for ID $productId. Skipping quantity suggestion.")
            return@combine null
        }

        val suggestions = suggestionList
            .associate { it.measurement to it.quantity }
            .toMutableMap()

        val default = defaultSuggestion()
        WeightMeasurementEnum.entries.forEach {
            if (!suggestions.containsKey(it)) {
                suggestions[it] = default[it] ?: error("Default suggestion not found for $it")
            }
        }

        QuantitySuggestion(
            product = product.toDomain(),
            quantitySuggestions = suggestions
        )
    }.filterNotNull()

    override fun observeProductQueries(limit: Int): Flow<List<ProductQuery>> {
        return addFoodDao.observeLatestQueries(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeMeasurementById(id: Long): Flow<ProductWithWeightMeasurement?> {
        return addFoodDao.observeMeasurement(id).map {
            it?.toDomain()
        }
    }

    override fun observeMeasurementByProductId(productId: Long): Flow<ProductWithWeightMeasurement?> {
        return productDao.observeProductById(productId).map {
            if (it == null) {
                return@map null
            }

            ProductWithWeightMeasurement(
                product = it.toDomain(),
                measurementId = null,
                measurement = WeightMeasurement.WeightUnit(100f)
            )
        }
    }

    private companion object {
        private const val TAG = "AddFoodRepositoryImpl"
        private const val NETWORK_PAGE_SIZE = 30
    }
}
