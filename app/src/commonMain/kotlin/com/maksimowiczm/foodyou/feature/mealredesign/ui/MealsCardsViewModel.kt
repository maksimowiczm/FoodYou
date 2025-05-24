package com.maksimowiczm.foodyou.feature.mealredesign.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.Measurement
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.mealredesign.domain.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

internal class MealsCardsViewModel : ViewModel() {

    fun observeMeals(date: LocalDate): Flow<List<Meal>> = flowOf(
        listOf(
            Meal(
                id = 1L,
                name = "Breakfast",
                from = LocalTime(6, 0),
                to = LocalTime(10, 0),
                food = listOf(
                    ProductWithMeasurement(
                        measurementId = MeasurementId.Product(1L),
                        measurement = Measurement.Gram(100f),
                        measurementDate = LocalDateTime(2025, 5, 24, 13, 15),
                        product = Product(
                            id = FoodId.Product(1L),
                            name = "Oatmeal",
                            brand = "Brand A",
                            nutritionFacts = testNutritionFacts(),
                            barcode = null,
                            packageWeight = null,
                            servingWeight = null
                        )
                    ),
                    ProductWithMeasurement(
                        measurementId = MeasurementId.Product(1L),
                        measurement = Measurement.Serving(1f),
                        measurementDate = LocalDateTime(2025, 5, 24, 13, 15),
                        product = Product(
                            id = FoodId.Product(1L),
                            name = "Yogurt",
                            brand = "Brand B",
                            nutritionFacts = testNutritionFacts(),
                            barcode = null,
                            packageWeight = null,
                            servingWeight = PortionWeight.Serving(100f)
                        )
                    )
//            ProductWithMeasurement(
//                measurementId = MeasurementId.Product(1L),
//                measurement = Measurement.Serving(1f),
//                measurementDate = LocalDateTime(2025, 5, 24, 13, 15),
//                product = Product(
//                    id = FoodId.Product(1L),
//                    name = "Yogurt",
//                    brand = "Brand B",
//                    nutritionFacts = testNutritionFacts(),
//                    barcode = null,
//                    packageWeight = null,
//                    servingWeight = null
//                )
//            )
                )
            )
        )
    )
}
