package com.maksimowiczm.foodyou.core.model

/**
 * Flat recipe is a simplified version of a recipe that does not contain any ingredients.
 */
data class FlatRecipe(
    override val id: FoodId,
    override val name: String,
    override val nutrients: Nutrients,
    override val packageWeight: PortionWeight.Package?,
    override val servingWeight: PortionWeight.Serving?
) : Food {
    override val brand = null
}
