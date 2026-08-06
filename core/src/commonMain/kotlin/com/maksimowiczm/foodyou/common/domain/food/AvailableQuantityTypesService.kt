package com.maksimowiczm.foodyou.common.domain.food

/**
 * Service to determine the available quantity types for a food item.
 *
 * It filters quantity types based on physical state (liquid vs. solid) and availability of metadata
 * like serving or package sizes.
 */
object AvailableQuantityTypesService {

    /**
     * Returns a list of [QuantityType]s that can be used for the given food.
     *
     * @param isLiquid Whether the food is a liquid.
     * @param servingQuantity The absolute quantity representing one serving, if known.
     * @param packageQuantity The absolute quantity representing one package, if known.
     */
    fun getAvailableTypes(
        isLiquid: Boolean,
        servingQuantity: AbsoluteQuantity?,
        packageQuantity: AbsoluteQuantity?,
    ): List<QuantityType> = buildList {
        if (isLiquid) {
            add(QuantityType.Milliliter)
            add(QuantityType.FluidOunce)
        } else {
            add(QuantityType.Gram)
            add(QuantityType.Ounce)
        }
        if (servingQuantity != null) add(QuantityType.Serving)
        if (packageQuantity != null) add(QuantityType.Package)
    }
}
