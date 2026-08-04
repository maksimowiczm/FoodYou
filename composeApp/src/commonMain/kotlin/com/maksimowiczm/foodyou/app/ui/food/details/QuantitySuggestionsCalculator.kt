package com.maksimowiczm.foodyou.app.ui.food.details

import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters

object QuantitySuggestionsCalculator {

    /**
     * Returns a list of suggested [Quantity]s for the given food parameters.
     *
     * @param isLiquid Whether the food is a liquid.
     * @param servingQuantity The absolute quantity representing one serving.
     * @param packageQuantity The absolute quantity representing one package.
     */
    fun getSuggestions(
        isLiquid: Boolean,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
    ): List<Quantity> = buildList {
        if (isLiquid) add(AbsoluteQuantity.Volume(100.milliliters))
        else add(AbsoluteQuantity.Weight(100.grams))

        if (servingQuantity != null) add(ServingQuantity(1.0))
        if (packageQuantity != null) add(PackageQuantity(1.0))
    }
}
