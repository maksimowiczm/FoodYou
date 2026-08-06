package com.maksimowiczm.foodyou.features.food.details

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters

/**
 * Utility for calculating the initial [Quantity] to be displayed when a user interacts with food
 * details.
 */
object InitialQuantityCalculator {

    /**
     * Calculates the initial [Quantity] based on the available food metadata.
     *
     * @param servingQuantity The absolute size of a single serving, or null if not defined.
     * @param packageQuantity The absolute size of the whole package, or null if not defined.
     * @param isLiquid Whether the food item is a liquid.
     * @return The calculated initial [Quantity].
     */
    fun calculate(
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
        isLiquid: Boolean,
    ): Quantity =
        servingQuantity?.let { ServingQuantity(1.0) }
            ?: packageQuantity?.let { PackageQuantity(1.0) }
            ?: if (isLiquid) AbsoluteQuantity.Volume(100.milliliters)
            else AbsoluteQuantity.Weight(100.grams)
}
