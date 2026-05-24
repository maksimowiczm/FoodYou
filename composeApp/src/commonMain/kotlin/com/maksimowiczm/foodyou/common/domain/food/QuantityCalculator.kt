package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.map

object QuantityCalculator {

    /**
     * Tries to convert [Quantity] to [AbsoluteQuantity] based on the food's serving or package
     * quantity.
     */
    fun calculateAbsoluteQuantity(
        suggestedQuantity: Quantity,
        packageQuantity: AbsoluteQuantity?,
        servingQuantity: AbsoluteQuantity?,
    ): Result<AbsoluteQuantity, Error> {
        val scaled =
            when (suggestedQuantity) {
                is AbsoluteQuantity -> suggestedQuantity
                is PackageQuantity ->
                    scale(
                        packageQuantity ?: return Err(Error.NoPackageQuantity),
                        suggestedQuantity.packages,
                    )

                is ServingQuantity ->
                    scale(
                        servingQuantity ?: return Err(Error.NoServingQuantity),
                        suggestedQuantity.servings,
                    )
            }

        return Ok(scaled)
    }

    fun calculateAbsoluteQuantity(
        baseQuantity: AbsoluteQuantity,
        quantity: PackageQuantity,
    ): AbsoluteQuantity = scale(baseQuantity, quantity.packages)

    fun calculateAbsoluteQuantity(
        baseQuantity: AbsoluteQuantity,
        quantity: ServingQuantity,
    ): AbsoluteQuantity = scale(baseQuantity, quantity.servings)

    private fun scale(quantity: AbsoluteQuantity, factor: Double): AbsoluteQuantity {
        return when (quantity) {
            is AbsoluteQuantity.Volume -> AbsoluteQuantity.Volume(quantity.volume * factor)
            is AbsoluteQuantity.Weight -> AbsoluteQuantity.Weight(quantity.weight * factor)
        }
    }

    sealed interface Error {
        data object NoPackageQuantity : Error

        data object NoServingQuantity : Error
    }
}

/**
 * Scales this [NutritionFacts] by the given [quantity], using [packageQuantity] and
 * [servingQuantity] as reference sizes when the quantity is package- or serving-based.
 *
 * Resolves [quantity] to an [AbsoluteQuantity] via [QuantityCalculator].
 *
 * @param packageQuantity the absolute size of one package, or null if unknown.
 * @param servingQuantity the absolute size of one serving, or null if unknown.
 * @param quantity the amount of food to calculate nutrition facts for.
 * @return a new [NutritionFacts] scaled to the given [quantity], or a [QuantityCalculator.Error] if
 *   [quantity] is package- or serving-based and the corresponding reference size is null.
 */
fun NutritionFacts.scale(
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    quantity: Quantity,
): Result<NutritionFacts, QuantityCalculator.Error> =
    QuantityCalculator.calculateAbsoluteQuantity(
            suggestedQuantity = quantity,
            packageQuantity = packageQuantity,
            servingQuantity = servingQuantity,
        )
        .map {
            val multiplier =
                when (it) {
                    is AbsoluteQuantity.Volume -> it.volume.milliliters / 100
                    is AbsoluteQuantity.Weight -> it.weight.grams / 100
                }

            this * multiplier
        }
