package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

/**
 * Represents the quantity of a food component, expressed in one of several measurement forms.
 *
 * Each variant ultimately resolves to an [absoluteWeight], allowing uniform nutritional
 * calculations regardless of how the quantity was originally specified.
 */
@Serializable
sealed interface FoodComponentComponentQuantity {
    /** The total weight of this component, derived from the specific quantity representation. */
    val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight

    /**
     * A quantity expressed as a direct weight measurement.
     *
     * @property weight The explicit weight of the component.
     */
    @Serializable
    data class Weight(val weight: com.maksimowiczm.foodyou.common.domain.Weight) :
        FoodComponentComponentQuantity {
        override val absoluteWeight = weight
    }

    /**
     * A quantity expressed as a number of packages.
     *
     * @property quantity The number of packages.
     * @property packageWeight The weight of a single package.
     */
    @Serializable
    data class Package(
        val quantity: Double,
        val packageWeight: com.maksimowiczm.foodyou.common.domain.Weight,
    ) : FoodComponentComponentQuantity {
        override val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight =
            packageWeight * quantity
    }

    /**
     * A quantity expressed as a number of servings.
     *
     * @property quantity The number of servings.
     * @property servingWeight The weight of a single serving.
     */
    @Serializable
    data class Serving(
        val quantity: Double,
        val servingWeight: com.maksimowiczm.foodyou.common.domain.Weight,
    ) : FoodComponentComponentQuantity {
        override val absoluteWeight: com.maksimowiczm.foodyou.common.domain.Weight =
            servingWeight * quantity
    }
}

fun FoodComponentComponentQuantity.toQuantity(): Quantity =
    when (this) {
        is FoodComponentComponentQuantity.Weight -> AbsoluteQuantity.Weight(weight)
        is FoodComponentComponentQuantity.Package -> PackageQuantity(quantity)
        is FoodComponentComponentQuantity.Serving -> ServingQuantity(quantity)
    }
