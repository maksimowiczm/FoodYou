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
    fun calculateAbsoluteQuantity(food: SearchableFoodDto): Result<AbsoluteQuantity, Error> {
        return when (food.suggestedQuantity) {
            is AbsoluteQuantity -> Ok(food.suggestedQuantity)
            is PackageQuantity -> calculateAbsoluteQuantity(food, food.suggestedQuantity)
            is ServingQuantity -> calculateAbsoluteQuantity(food, food.suggestedQuantity)
        }
    }

    fun calculateAbsoluteQuantity(
        food: SearchableFoodDto,
        quantity: PackageQuantity,
    ): Result<AbsoluteQuantity, Error.NoPackageQuantity> {
        val packageQuantity = food.packageQuantity ?: return Err(Error.NoPackageQuantity)
        return Ok(scale(packageQuantity, quantity.packages))
    }

    fun calculateAbsoluteQuantity(
        food: SearchableFoodDto,
        quantity: ServingQuantity,
    ): Result<AbsoluteQuantity, Error.NoServingQuantity> {
        val servingQuantity = food.servingQuantity ?: return Err(Error.NoServingQuantity)
        return Ok(scale(servingQuantity, quantity.servings))
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
