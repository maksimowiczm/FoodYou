package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases

import androidx.paging.PagingData
import androidx.paging.flatMap
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.SearchRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
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
                val mid = it.filter {
                    when (p.foodId) {
                        is FoodId.Product -> it.product.id == p.foodId.productId
                        is FoodId.Recipe -> TODO()
                    }
                }

                if (mid.isEmpty()) {
                    val listId = when (p.foodId) {
                        is FoodId.Product -> "p_${p.foodId.productId}"
                        is FoodId.Recipe -> "r_${p.foodId.recipeId}"
                    }

                    val weight = when (p.measurement) {
                        is WeightMeasurement.Package -> {
                            assert(p.packageWeight != null) {
                                "Package weight should not be null for package measurement"
                            }
                            p.packageWeight!! * p.measurement.quantity
                        }

                        is WeightMeasurement.Serving -> {
                            assert(p.servingWeight != null) {
                                "Serving weight should not be null for serving measurement"
                            }
                            p.servingWeight!! * p.measurement.quantity
                        }

                        is WeightMeasurement.WeightUnit -> {
                            p.measurement.weight
                        }
                    }

                    val calories = p.calories * weight / 100f
                    val proteins = p.proteins * weight / 100f
                    val carbohydrates = p.carbohydrates * weight / 100f
                    val fats = p.fats * weight / 100f

                    listOf(
                        AddFoodSearchListItem(
                            id = p.foodId,
                            listId = listId,
                            name = p.name,
                            brand = p.brand,
                            calories = calories.roundToInt(),
                            proteins = proteins.roundToInt(),
                            carbohydrates = carbohydrates.roundToInt(),
                            fats = fats.roundToInt(),
                            weightMeasurement = p.measurement,
                            weight = weight,
                            measurementId = null
                        )
                    )
                } else {
                    mid.map { m ->
                        val listId = when (p.foodId) {
                            is FoodId.Product -> "p_${p.foodId.productId}_${m.measurementId}"
                            is FoodId.Recipe -> "r_${p.foodId.recipeId}_${m.measurementId}"
                        }

                        val weight = when (m.measurement) {
                            is WeightMeasurement.Package -> {
                                assert(p.packageWeight != null) {
                                    "Package weight should not be null for package measurement"
                                }
                                p.packageWeight!! * m.measurement.quantity
                            }

                            is WeightMeasurement.Serving -> {
                                assert(p.servingWeight != null) {
                                    "Serving weight should not be null for serving measurement"
                                }
                                p.servingWeight!! * m.measurement.quantity
                            }

                            is WeightMeasurement.WeightUnit -> {
                                m.measurement.weight
                            }
                        }

                        val calories = p.calories * weight / 100f
                        val proteins = p.proteins * weight / 100f
                        val carbohydrates = p.carbohydrates * weight / 100f
                        val fats = p.fats * weight / 100f

                        AddFoodSearchListItem(
                            id = p.foodId,
                            listId = listId,
                            name = p.name,
                            brand = p.brand,
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
        }
    }
}
