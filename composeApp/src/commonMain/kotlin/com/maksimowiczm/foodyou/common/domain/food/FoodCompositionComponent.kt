package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.sum
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

/**
 * A single ingredient or sub-recipe that contributes to a food composition.
 *
 * @property identity Unique identifier for this component
 * @property name Human-readable name of the food
 * @property image Optional image of the food
 * @property nutritionFacts Nutrition facts per 100 g of this component, before servings scaling
 * @property measuredNutritionFacts **absolute** nutrition delivered by this component at its actual
 *   [quantity]
 * @property quantity How much of this ingredient is used
 * @property allIdentities Set of all [FoodCompositionComponentIdentity]s that make up this
 *   component, including itself and any nested components
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
     * A composite component backed by a nested list of components (i.e. a sub-recipe).
     *
     * This allows for recursive food structures, such as a "Sandwich" containing "Bread" and
     * "Butter", where "Bread" could itself be a [Composite] component.
     *
     * @property components The list of components that make up this composite
     */
    @Serializable
    data class Composite(
        override val identity: FoodCompositionComponentIdentity.Composite,
        override val name: FoodName,
        override val image: FoodCompositionComponentImage?,
        override val quantity: FoodComponentComponentQuantity,
        val components: List<FoodCompositionComponent>,
    ) : FoodCompositionComponent {
        override val nutritionFacts: NutritionFacts = components.nutritionFacts
        override val measuredNutritionFacts: NutritionFacts =
            nutritionFacts * quantity.absoluteWeight.grams / 100.0
        override val allIdentities: Set<FoodCompositionComponentIdentity> =
            setOf(identity) + components.allComponentIdentities
    }
}

/** Sum of the absolute weights of all components. */
val Iterable<FoodCompositionComponent>.totalWeight: Weight
    get() = map { it.quantity.absoluteWeight }.sum()

/** Set of all identities present in these components and their subcomponents. */
val Iterable<FoodCompositionComponent>.allComponentIdentities: Set<FoodCompositionComponentIdentity>
    get() = flatMap { it.allIdentities }.toSet()

/**
 * Nutrition facts normalized to 100 g of this food composition.
 *
 * ```
 * nutritionFacts = Σ(measuredNutritionFacts) / totalWeight.grams * 100
 * ```
 */
val Iterable<FoodCompositionComponent>.nutritionFacts: NutritionFacts
    get() =
        if (count() == 0) NutritionFacts.zeroCompleted
        else map { it.measuredNutritionFacts }.sum() / totalWeight.grams * 100.0
