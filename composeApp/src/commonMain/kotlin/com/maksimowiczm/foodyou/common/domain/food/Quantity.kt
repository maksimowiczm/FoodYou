package com.maksimowiczm.foodyou.common.domain.food

import kotlinx.serialization.Serializable

/** Represents a quantity of a food item. It can be an absolute quantity (weight or volume), */
@Serializable sealed interface Quantity

/** Represents an absolute quantity, which can be either weight or volume. */
@Serializable
sealed interface AbsoluteQuantity : Quantity {
    @Serializable
    data class Weight(val weight: com.maksimowiczm.foodyou.common.domain.Weight) : AbsoluteQuantity

    @Serializable
    data class Volume(val volume: com.maksimowiczm.foodyou.common.domain.Volume) : AbsoluteQuantity

    operator fun div(divisor: Double): AbsoluteQuantity =
        when (this) {
            is Weight -> Weight(weight / divisor)
            is Volume -> Volume(volume / divisor)
        }
}

/** Represents a quantity in terms of packages. For example, "2 packages", "0.5 packages". */
@Serializable data class PackageQuantity(val packages: Double) : Quantity

/** Represents a quantity in terms of servings. For example, "3 servings", "0.75 servings". */
@Serializable data class ServingQuantity(val servings: Double) : Quantity
