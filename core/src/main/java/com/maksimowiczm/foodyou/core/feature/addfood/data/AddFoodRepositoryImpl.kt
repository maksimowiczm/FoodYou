package com.maksimowiczm.foodyou.core.feature.addfood.data

import android.util.Log
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
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
import com.maksimowiczm.foodyou.core.infrastructure.database.FoodYouDatabase
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
    private val foodYouDatabase: FoodYouDatabase,
//    private val productRemoteMediatorFactory: ProductRemoteMediatorFactory,
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

    override suspend fun removeMeasurement(portionId: Long) {
        val entity = addFoodDao.observeWeightMeasurement(portionId).first()

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

    private fun AddFoodDao.observePagedProductsWithMeasurementByQuery(
        mealId: Long,
        date: LocalDate,
        query: String?
    ) = observePagedProductsWithMeasurement(
        mealId = mealId,
        epochDay = date.toEpochDays(),
        query = query,
        barcode = null
    )

    private fun AddFoodDao.observePagedProductsWithMeasurementByBarcode(
        mealId: Long,
        date: LocalDate,
        barcode: String
    ) = observePagedProductsWithMeasurement(
        mealId = mealId,
        epochDay = date.toEpochDays(),
        query = null,
        barcode = barcode
    )

    private companion object {
        private const val TAG = "AddFoodRepositoryImpl"
    }
}
