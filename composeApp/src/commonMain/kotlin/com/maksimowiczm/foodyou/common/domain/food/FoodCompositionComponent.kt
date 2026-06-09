package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

/**
 * A single ingredient or sub-recipe that contributes to a [FoodComposition].
 *
 * @property nutritionFacts Nutrition facts per 100 g of this component, before quantity scaling
 * @property quantity How much of this ingredient is used
 * @property measuredNutritionFacts **absolute** nutrition delivered by this component at its actual
 *   [quantity]
 */
@Serializable
sealed interface FoodCompositionComponent {
    val identity: FoodCompositionComponentIdentity
    val name: FoodName

    val image: FoodCompositionComponentImage?

    val nutritionFacts: NutritionFacts

    val measuredNutritionFacts: NutritionFacts

    val quantity: FoodComponentComponentQuantity

    val allIdentities: Set<FoodCompositionComponentIdentity>

    /** A leaf component backed by a single food item with known per-100g nutrition. */
    @Serializable
    data class Simple(
        override val identity: FoodCompositionComponentIdentity.Leaf,
        override val name: FoodName,
        override val image: FoodCompositionComponentImage?,
        override val nutritionFacts: NutritionFacts,
        override val quantity: FoodComponentComponentQuantity,
    ) : FoodCompositionComponent {
        override val measuredNutritionFacts: NutritionFacts =
            nutritionFacts * quantity.absoluteWeight.grams / 100.0

        override val allIdentities: Set<FoodCompositionComponentIdentity> = setOf(identity)
    }

    /** A composite component backed by a nested [FoodComposition] (i.e. a sub-recipe). */
    @Serializable
    data class Composite(
        override val identity: FoodCompositionComponentIdentity.Composite,
        override val name: FoodName,
        override val image: FoodCompositionComponentImage?,
        override val quantity: FoodComponentComponentQuantity,
        val composition: FoodComposition,
    ) : FoodCompositionComponent {
        init {
            require(identity !in composition.allComponentIdentities) {
                "Circular dependency detected: $identity is already present in its own composition"
            }
        }

        override val nutritionFacts: NutritionFacts = composition.nutritionFacts
        override val measuredNutritionFacts: NutritionFacts =
            nutritionFacts * quantity.absoluteWeight.grams / 100.0

        override val allIdentities: Set<FoodCompositionComponentIdentity> =
            setOf(identity) + composition.allComponentIdentities
    }
}
