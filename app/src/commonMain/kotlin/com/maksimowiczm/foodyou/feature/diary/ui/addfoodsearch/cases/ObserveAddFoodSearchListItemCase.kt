package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases

import androidx.paging.PagingData
import androidx.paging.flatMap
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchModel
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import kotlin.collections.map
import kotlin.math.roundToInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveAddFoodSearchListItemCase(
    private val searchRepository: SearchRepository,
    private val measurementRepository: MeasurementRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        query: String?,
        mealId: Long,
        date: LocalDate
    ): Flow<PagingData<AddFoodSearchListItem>> = searchRepository.queryProducts(
        query = query
    ).flatMapLatest { pagingData ->
        measurementRepository.observeMeasurements(
            mealId = mealId,
            date = date
        ).map { measurements ->
            pagingData.flatMap { p ->
                handle(measurements, p)
            }
        }
    }
}

private fun handle(
    measurements: List<FoodMeasurement>,
    searchModel: SearchModel
): List<AddFoodSearchListItem> {
    val ids = measurements.filter { it.foodId == searchModel.foodId }

    return if (ids.isEmpty()) {
        listOf(handleEmpty(searchModel))
    } else {
        // Unfold the measurements

        ids.map { m ->
            val weight = m.measurement.getWeight(
                packageWeight = searchModel.packageWeight,
                servingWeight = searchModel.servingWeight
            )

            val calories = searchModel.calories * weight / 100f
            val proteins = searchModel.proteins * weight / 100f
            val carbohydrates = searchModel.carbohydrates * weight / 100f
            val fats = searchModel.fats * weight / 100f

            AddFoodSearchListItem(
                id = searchModel.foodId,
                listId = searchModel.foodId.uniqueId(m.measurementId),
                name = searchModel.name,
                brand = searchModel.brand,
                calories = calories.roundToInt(),
                proteins = proteins.roundToInt(),
                carbohydrates = carbohydrates.roundToInt(),
                fats = fats.roundToInt(),
                weightMeasurement = m.measurement,
                weight = weight,
                measurementId = m.measurementId
            )
        }
    }
}

private fun handleEmpty(searchModel: SearchModel): AddFoodSearchListItem {
    val weight = searchModel.measurement.getWeight(
        packageWeight = searchModel.packageWeight,
        servingWeight = searchModel.servingWeight
    )

    val calories = searchModel.calories * weight / 100f
    val proteins = searchModel.proteins * weight / 100f
    val carbohydrates = searchModel.carbohydrates * weight / 100f
    val fats = searchModel.fats * weight / 100f

    return AddFoodSearchListItem(
        id = searchModel.foodId,
        listId = searchModel.foodId.uniqueId(null),
        name = searchModel.name,
        brand = searchModel.brand,
        calories = calories.roundToInt(),
        proteins = proteins.roundToInt(),
        carbohydrates = carbohydrates.roundToInt(),
        fats = fats.roundToInt(),
        weightMeasurement = searchModel.measurement,
        weight = weight,
        measurementId = null
    )
}

private fun FoodId.uniqueId(measurementId: MeasurementId?): String {
    val measurementId = measurementId?.let {
        when (it) {
            is MeasurementId.Product -> it.measurementId
            is MeasurementId.Recipe -> it.measurementId
        }
    }

    return when (this) {
        is FoodId.Product if (measurementId != null) -> "p_${productId}_$measurementId"
        is FoodId.Product -> "p_$productId"
        is FoodId.Recipe if (measurementId != null) -> "r_${recipeId}_$measurementId"
        is FoodId.Recipe -> "r_$recipeId"
    }
}
