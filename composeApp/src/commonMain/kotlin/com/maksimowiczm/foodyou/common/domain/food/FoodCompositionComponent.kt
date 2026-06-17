package com.maksimowiczm.foodyou.common.domain.food

import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

/**
 * A single ingredient or sub-recipe that contributes to a [FoodComposition].
 *
 * @property identity Unique identifier for this component
 * @property name Human-readable name of the food
 * @property image Optional image of the food
 * @property nutritionFacts Nutrition facts per 100 g of this component, before quantity scaling
 * @property measuredNutritionFacts **absolute** nutrition delivered by this component at its actual
 * @property quantity How much of this ingredient is used
 * @property allIdentities Set of all [FoodCompositionComponentIdentity]s that make up this
 *   component, including itself and any nested components [quantity]
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

    /** Creates a copy of this component with an anonymous identity. */
    fun anonymize(): FoodCompositionComponent =
        Anonymous(
            identity = FoodCompositionComponentIdentity.Anonymous(Uuid.random()),
            name = name,
            image = image,
            nutritionFacts = nutritionFacts,
            quantity = quantity,
        )

    /** A component that is not backed by any persistent food item. */
    @Serializable
    data class Anonymous(
        override val identity: FoodCompositionComponentIdentity.Anonymous,
        override val name: FoodName,
        override val image: FoodCompositionComponentImage?,
        override val nutritionFacts: NutritionFacts,
        override val quantity: FoodComponentComponentQuantity,
    ) : FoodCompositionComponent {
        override val measuredNutritionFacts: NutritionFacts =
            nutritionFacts * quantity.absoluteWeight.grams / 100.0
        override val allIdentities: Set<FoodCompositionComponentIdentity.Identified> = emptySet()
    }

    /**
     * A leaf component backed by a single food item with known per-100g nutrition.
     *
     * This represents a basic ingredient like "Apple" or "Chicken Breast".
     */
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
        override val allIdentities: Set<FoodCompositionComponentIdentity.Leaf> = setOf(identity)
    }

    /**
     * A composite component backed by a nested [FoodComposition] (i.e. a sub-recipe).
     *
     * This allows for recursive food structures, such as a "Sandwich" containing "Bread" and
     * "Butter", where "Bread" could itself be a [Composite] component.
     *
     * @property composition The nested food composition that defines this component's nutrition
     */
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
