package com.maksimowiczm.foodyou.business.food.domain

import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.food.Weighted
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * @param id Unique identifier for the food item.
 * @param headline A short description or name of the food item.
 * @param totalWeight Total weight of the food item in grams, or null if not applicable.
 * @param servingWeight Weight of a single serving of the food item in grams, or null if not
 *   applicable.
 * @param nutritionFacts Nutritional information for the food item per 100 grams or milliliters.
 * @param isLiquid Indicates whether the food item is a liquid (e.g., soup, juice) or solid (e.g.,
 *   bread, meat).
 */
sealed interface Food : Weighted {
    val id: FoodId
    val headline: String
    override val totalWeight: Double?
    override val servingWeight: Double?
    val nutritionFacts: NutritionFacts
    val isLiquid: Boolean
}

// These extensions will probably be moved into business when user would be able to choose between
// metric and imperial measurements. This is why they are wrapped in Flow, so they can be
// easily converted to the appropriate measurement system later.
val Food.possibleMeasurementTypes: Flow<List<MeasurementType>>
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

val Food.defaultMeasurement: Flow<Measurement>
    get() =
        flowOf(
            when {
                servingWeight != null -> Measurement.Serving(1.0)
                totalWeight != null -> Measurement.Package(1.0)
                isLiquid -> Measurement.Milliliter(100.0)
                else -> Measurement.Gram(100.0)
            }
        )
