package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases

import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveAddFoodSearchListItemCase(private val searchRepository: SearchRepository) {
    operator fun invoke(
        query: String?,
        mealId: Long,
        date: LocalDate
    ): Flow<PagingData<AddFoodSearchListItem>> = searchRepository.queryProducts(
        mealId = mealId,
        date = date,
        query = query
    ).map { data ->
        data.map { p ->
            val weight = when (p.weightMeasurement) {
                is WeightMeasurement.Package -> {
                    assert(p.packageWeight != null) {
                        "Package weight should not be null for package measurement"
                    }
                    p.packageWeight!!
                }

                is WeightMeasurement.Serving -> {
                    assert(p.servingWeight != null) {
                        "Serving weight should not be null for serving measurement"
                    }
                    p.servingWeight!!
                }

                is WeightMeasurement.WeightUnit -> {
                    p.weightMeasurement.weight
                }
            }

            val calories = p.calories * weight / 100f
            val proteins = p.proteins * weight / 100f
            val carbohydrates = p.carbohydrates * weight / 100f
            val fats = p.fats * weight / 100f

            AddFoodSearchListItem(
                id = p.foodId,
                listId = p.uniqueId,
                name = p.name,
                brand = p.brand,
                calories = calories.roundToInt(),
                proteins = proteins.roundToInt(),
                carbohydrates = carbohydrates.roundToInt(),
                fats = fats.roundToInt(),
                weightMeasurement = p.weightMeasurement,
                weight = weight,
                measurementId = p.measurementId
            )
        }
    }
}
