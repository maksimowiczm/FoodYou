package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases

import androidx.paging.PagingData
import androidx.paging.flatMap
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryMeasuredProduct
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchModel
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
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
        ).map {
            pagingData.flatMap { p ->
                it.handle(p)
            }
        }
    }
}

private fun List<DiaryMeasuredProduct>.handle(
    searchModel: SearchModel
): List<AddFoodSearchListItem> {
    val mid = filter {
        when (searchModel.foodId) {
            is FoodId.Product -> it.product.id == searchModel.foodId.productId
            is FoodId.Recipe -> TODO()
        }
    }

    return if (mid.isEmpty()) {
        val listId = when (searchModel.foodId) {
            is FoodId.Product -> "p_${searchModel.foodId.productId}"
            is FoodId.Recipe -> "r_${searchModel.foodId.recipeId}"
        }

        val weight = when (searchModel.measurement) {
            is WeightMeasurement.Package -> {
                assert(searchModel.packageWeight != null) {
                    "Package weight should not be null for package measurement"
                }
                searchModel.packageWeight!! * searchModel.measurement.quantity
            }

            is WeightMeasurement.Serving -> {
                assert(searchModel.servingWeight != null) {
                    "Serving weight should not be null for serving measurement"
                }
                searchModel.servingWeight!! * searchModel.measurement.quantity
            }

            is WeightMeasurement.WeightUnit -> {
                searchModel.measurement.weight
            }
        }

        val calories = searchModel.calories * weight / 100f
        val proteins = searchModel.proteins * weight / 100f
        val carbohydrates = searchModel.carbohydrates * weight / 100f
        val fats = searchModel.fats * weight / 100f

        listOf(
            AddFoodSearchListItem(
                id = searchModel.foodId,
                listId = listId,
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
        )
    } else {
        mid.map { m ->
            val listId = when (searchModel.foodId) {
                is FoodId.Product -> "p_${searchModel.foodId.productId}_${m.measurementId}"
                is FoodId.Recipe -> "r_${searchModel.foodId.recipeId}_${m.measurementId}"
            }

            val weight = when (m.measurement) {
                is WeightMeasurement.Package -> {
                    assert(searchModel.packageWeight != null) {
                        "Package weight should not be null for package measurement"
                    }
                    searchModel.packageWeight!! * m.measurement.quantity
                }

                is WeightMeasurement.Serving -> {
                    assert(searchModel.servingWeight != null) {
                        "Serving weight should not be null for serving measurement"
                    }
                    searchModel.servingWeight!! * m.measurement.quantity
                }

                is WeightMeasurement.WeightUnit -> {
                    m.measurement.weight
                }
            }

            val calories = searchModel.calories * weight / 100f
            val proteins = searchModel.proteins * weight / 100f
            val carbohydrates = searchModel.carbohydrates * weight / 100f
            val fats = searchModel.fats * weight / 100f

            AddFoodSearchListItem(
                id = searchModel.foodId,
                listId = listId,
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
