package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapValues

/**
 * Represents a food item in the food diary.
 *
 * @param name The name of the food item.
 * @param nutritionFacts The nutrition facts of the food item per 100g or 100ml.
 * @param servingWeight The weight of a single serving of the food item, if applicable.
 * @param totalWeight The total weight of the food item, if applicable.
 * @param isLiquid Indicates whether the food item is a liquid.
 */
sealed interface DiaryFood {
    val name: String
    val nutritionFacts: NutritionFacts
    val servingWeight: Double?
    val totalWeight: Double?
    val isLiquid: Boolean
    val note: String?

    fun weight(measurement: Measurement): Double
}

// These extensions will probably be moved into business when user would be able to choose between
// metric and imperial measurements. This is why they are wrapped in Flow, so they can be
// easily converted to the appropriate measurement system later.

val DiaryFood.possibleMeasurementTypes: Flow<List<MeasurementType>>
    get() =
        flowOf(
            MeasurementType.entries.filter { type ->
                when (type) {
                    MeasurementType.Gram -> !isLiquid
                    MeasurementType.Ounce -> !isLiquid
                    MeasurementType.Milliliter -> isLiquid
                    MeasurementType.FluidOunce -> isLiquid
                    MeasurementType.Package -> totalWeight != null
                    MeasurementType.Serving -> servingWeight != null
                }
            }
        )

val DiaryFood.suggestions: Flow<List<Measurement>>
    get() =
        possibleMeasurementTypes.mapValues {
            when (it) {
                MeasurementType.Gram -> Measurement.Gram(100.0)
                MeasurementType.Ounce -> Measurement.Ounce(3.0)
                MeasurementType.Package -> Measurement.Package(1.0)
                MeasurementType.Serving -> Measurement.Serving(1.0)
                MeasurementType.Milliliter -> Measurement.Milliliter(100.0)
                MeasurementType.FluidOunce -> Measurement.FluidOunce(3.0)
            }
        }
