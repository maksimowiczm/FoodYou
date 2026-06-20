package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

/** Represents the quantity of a food component, expressed in one of several measurement forms. */
@Serializable
sealed interface FoodComponentComponentQuantity {
    /**
     * A quantity expressed as a direct weight measurement.
     *
     * @property weight The explicit weight of the component.
     */
    @Serializable
    data class Weight(val weight: com.maksimowiczm.foodyou.common.domain.Weight) :
        FoodComponentComponentQuantity

    /**
     * A quantity expressed as a number of packages.
     *
     * @property packages The number of packages.
     */
    @Serializable data class Package(val packages: Double) : FoodComponentComponentQuantity

    /**
     * A quantity expressed as a number of servings.
     *
     * @property servings The number of servings.
     */
    @Serializable data class Serving(val servings: Double) : FoodComponentComponentQuantity
}

fun FoodComponentComponentQuantity.toQuantity(): Quantity =
    when (this) {
        is FoodComponentComponentQuantity.Weight -> AbsoluteQuantity.Weight(weight)
        is FoodComponentComponentQuantity.Package -> PackageQuantity(packages)
        is FoodComponentComponentQuantity.Serving -> ServingQuantity(servings)
    }
