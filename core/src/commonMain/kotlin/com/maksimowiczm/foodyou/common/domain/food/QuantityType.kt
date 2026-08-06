package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.VolumeUnit
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.fluidOunces
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.domain.ounces

/**
 * Represents the type of unit used for a [Quantity].
 *
 * This enum maps to various weight and volume units, as well as food-specific units like servings
 * and packages.
 */
enum class QuantityType {
    Gram,
    Ounce,
    FluidOunce,
    Milliliter,
    Serving,
    Package,
}

/** Converts a numeric amount to a [Quantity] of this type. */
fun QuantityType.toQuantity(amount: Double): Quantity =
    when (this) {
        QuantityType.Gram -> AbsoluteQuantity.Weight(amount.grams)
        QuantityType.Ounce -> AbsoluteQuantity.Weight(amount.ounces)
        QuantityType.Milliliter -> AbsoluteQuantity.Volume(amount.milliliters)
        QuantityType.FluidOunce -> AbsoluteQuantity.Volume(amount.fluidOunces)
        QuantityType.Serving -> ServingQuantity(amount)
        QuantityType.Package -> PackageQuantity(amount)
    }

/**
 * Returns the numeric amount of this [Quantity] in its native unit.
 *
 * For example, if it's a [AbsoluteQuantity.Weight] in grams, it returns the number of grams.
 */
val Quantity.amount: Double
    get() =
        when (this) {
            is AbsoluteQuantity.Weight -> weight.toDouble(weight.unit)
            is AbsoluteQuantity.Volume -> volume.toDouble(volume.unit)
            is ServingQuantity -> servings
            is PackageQuantity -> packages
        }

/** Returns the [QuantityType] that represents the unit used in this [Quantity]. */
val Quantity.type: QuantityType
    get() =
        when (this) {
            is AbsoluteQuantity.Weight ->
                if (weight.unit == WeightUnit.Ounces) QuantityType.Ounce else QuantityType.Gram
            is AbsoluteQuantity.Volume ->
                if (volume.unit == VolumeUnit.FluidOunces) QuantityType.FluidOunce
                else QuantityType.Milliliter
            is ServingQuantity -> QuantityType.Serving
            is PackageQuantity -> QuantityType.Package
        }
