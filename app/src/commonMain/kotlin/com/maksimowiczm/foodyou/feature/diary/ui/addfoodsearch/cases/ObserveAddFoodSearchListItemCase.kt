package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.cases

import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.domain.QueryProductsUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model.AddFoodSearchListItem
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class ObserveAddFoodSearchListItemCase(private val queryProductsUseCase: QueryProductsUseCase) {
    operator fun invoke(
        query: String?,
        mealId: Long,
        date: LocalDate
    ): Flow<PagingData<AddFoodSearchListItem>> = queryProductsUseCase.queryProducts(
        mealId = mealId,
        date = date,
        query = query
    ).map { data ->
        data.map { p ->
            val listId = when (p) {
                is ProductWithMeasurement.Measurement -> "p_${p.measurementId}_${p.product.id}"
                is ProductWithMeasurement.Suggestion -> "p_${p.product.id}"
            }

            val measurementId = when (p) {
                is ProductWithMeasurement.Measurement -> MeasurementId.Product(p.measurementId)
                is ProductWithMeasurement.Suggestion -> null
            }

            val weight = p.measurement.getWeight(p.product)

            AddFoodSearchListItem(
                id = FoodId.Product(p.product.id),
                listId = listId,
                name = p.product.name,
                brand = p.product.brand,
                calories = p.calories.roundToInt(),
                proteins = p.proteins.roundToInt(),
                carbohydrates = p.carbohydrates.roundToInt(),
                fats = p.fats.roundToInt(),
                weightMeasurement = p.measurement,
                weight = weight,
                measurementId = measurementId
            )
        }
    }
}
