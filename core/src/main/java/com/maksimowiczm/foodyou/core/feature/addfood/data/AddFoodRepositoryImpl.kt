package com.maksimowiczm.foodyou.core.feature.addfood.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductIdWithMeasurementsId
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

class AddFoodRepositoryImpl(
    addFoodDatabase: AddFoodDatabase,
    productDatabase: ProductDatabase,
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
            createdAt = epochSeconds,
            rank = rankMeasurement(
                date = date,
                mealId = mealId,
                productId = productId
            )
        )

        return addFoodDao.insertWeightMeasurement(entity)
    }

    private suspend fun rankMeasurement(
        date: LocalDate,
        mealId: Long,
        productId: Long
    ): Float {
        val measurements = addFoodDao.getWeightMeasurements(
            mealId = mealId,
            epochDay = date.toEpochDays(),
            productId = productId,
            isDeleted = false
        )

        return measurements.maxOfOrNull { it.rank }?.plus(1)
            ?: WeightMeasurementEntity.FIRST_RANK
    }

    override fun queryProducts(
        mealId: Long,
        date: LocalDate,
        query: String?,
        localOnly: Boolean
    ): Flow<PagingData<ProductIdWithMeasurementsId>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true
            )
        ) {
            addFoodDao.observeProductIdsWithMeasurementIds(
                mealId = mealId,
                epochDay = date.toEpochDays(),
                query = query,
                barcode = null
            )
        }

        return pager.flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
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
        return combine(
            productDao.observeProductById(productId),
            addFoodDao.observeLatestMeasurementByProductId(productId)
        ) { productEntity, measurement ->
            if (productEntity == null) {
                Log.w(TAG, "Product not found for ID $productId. Skipping measurement.")
                return@combine null
            }

            if (measurement != null) {
                return@combine ProductWithWeightMeasurement(
                    product = productEntity.toDomain(),
                    measurement = measurement.toDomain().measurement
                )
            }

            val product = productEntity.toDomain()
            val weightMeasurement = WeightMeasurement.defaultForProduct(product)

            ProductWithWeightMeasurement(
                product = product,
                measurement = weightMeasurement
            )
        }
    }

    private companion object {
        private const val TAG = "AddFoodRepositoryImpl"
        private const val NETWORK_PAGE_SIZE = 30
    }
}
