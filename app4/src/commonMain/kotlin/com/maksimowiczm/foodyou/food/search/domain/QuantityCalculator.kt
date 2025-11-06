package com.maksimowiczm.foodyou.food.search.domain

import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.Quantity
import com.maksimowiczm.foodyou.common.domain.ServingQuantity

object QuantityCalculator {

    /**
     * Tries to convert [Quantity] to [AbsoluteQuantity] based on the food's serving or package
     * quantity.
     */
    fun calculateAbsoluteQuantity(
        quantity: Quantity,
        packageQuantity: AbsoluteQuantity?,
        servingQuantity: AbsoluteQuantity?,
    ): Result<AbsoluteQuantity, Error> {
        val scaled =
            when (quantity) {
                is AbsoluteQuantity -> quantity
                is PackageQuantity ->
                    scale(packageQuantity ?: return Err(Error.NoPackageQuantity), quantity.packages)

                is ServingQuantity ->
                    scale(servingQuantity ?: return Err(Error.NoServingQuantity), quantity.servings)
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
