package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result

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
