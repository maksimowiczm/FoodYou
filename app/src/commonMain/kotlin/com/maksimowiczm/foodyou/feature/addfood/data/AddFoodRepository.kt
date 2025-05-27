package com.maksimowiczm.foodyou.feature.addfood.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.data.model.search.SearchQueryEntity
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.SearchLocalDataSource
import com.maksimowiczm.foodyou.core.ext.mapValues
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

internal class AddFoodRepository(
    private val searchLocalDataSource: SearchLocalDataSource,
    private val foodLocalDataSource: FoodLocalDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val measurementMapper: MeasurementMapper = MeasurementMapper
) {
    private val ioScope = CoroutineScope(ioDispatcher + SupervisorJob())

    @OptIn(ExperimentalPagingApi::class)
    fun queryFood(query: String?, mealId: Long, date: LocalDate): Flow<PagingData<SearchFoodItem>> {
        val barcode = query?.takeIf { it.all { it.isDigit() } }

        // Insert query if it's not a barcode and not empty
        if (barcode == null && query?.isNotBlank() == true) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = 20
            )
        ) {
            if (barcode != null) {
                foodLocalDataSource.queryFoodByBarcode(
                    barcode = barcode,
                    mealId = mealId,
                    epochDay = date.toEpochDays()
                )
            } else {
                foodLocalDataSource.queryFood(
                    query = query,
                    mealId = mealId,
                    epochDay = date.toEpochDays()
                )
            }
        }.flow.mapValues { measurementMapper.toSearchFoodItem(it) }
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

        searchLocalDataSource.upsert(
            SearchQueryEntity(
                query = query,
                epochSeconds = epochSeconds
            )
        )
    }
}

private fun MeasurementMapper.toSearchFoodItem(entity: FoodSearchEntity) = with(entity) {
    when {
        productId != null && recipeId == null -> {
            val foodId = FoodId.Product(productId)
            val measurementId = measurementId?.let { MeasurementId.Product(measurementId) }

            SearchFoodItem(
                foodId = foodId,
                headline = brand?.let { "$name ($brand)" } ?: name,
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                packageWeight = packageWeight?.let { PortionWeight.Package(it) },
                servingWeight = servingWeight?.let { PortionWeight.Serving(it) },
                measurementId = measurementId,
                measurement = with(MeasurementMapper) { toMeasurement() },
                uniqueId = uiId
            )
        }

        recipeId != null && productId == null -> {
            val foodId = FoodId.Recipe(recipeId)
            val measurementId = measurementId?.let { MeasurementId.Recipe(measurementId) }

            SearchFoodItem(
                foodId = foodId,
                headline = brand?.let { "$name ($brand)" } ?: name,
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                packageWeight = packageWeight?.let { PortionWeight.Package(it) },
                servingWeight = servingWeight?.let { PortionWeight.Serving(it) },
                measurementId = measurementId,
                measurement = with(MeasurementMapper) { toMeasurement() },
                uniqueId = uiId
            )
        }

        else -> error("Data inconsistency: productId and recipeId are null")
    }
}
