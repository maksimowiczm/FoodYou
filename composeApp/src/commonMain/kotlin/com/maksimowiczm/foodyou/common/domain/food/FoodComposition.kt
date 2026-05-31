package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.sum
import kotlinx.serialization.Serializable

/**
 * Represents the nutritional composition of a food item made up of one or more components.
 *
 * @property components The non-empty list of components that make up this food.
 * @throws IllegalArgumentException if [components] is empty.
 */
@Serializable
data class FoodComposition(val components: List<FoodCompositionComponent>) {
    init {
        require(components.isNotEmpty()) { "FoodComposition must have at least one component" }
    }

    /** Sum of the absolute weights of all components. */
    val totalWeight: Weight = components.map { it.quantity.absoluteWeight }.sum()

    val allComponentIdentities: Set<FoodCompositionComponentIdentity> =
        components.flatMap { it.allIdentities }.toSet()

    /**
     * Nutrition facts normalized to 100 g of this food.
     *
     * ```
     * nutritionFacts = Σ(measuredNutritionFacts) / totalWeight.grams * 100
     * ```
     */
    val nutritionFacts: NutritionFacts =
        components.map { it.measuredNutritionFacts }.sum() / totalWeight.grams * 100.0
}
