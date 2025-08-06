package com.maksimowiczm.foodyou.business.food.domain

/**
 * Represents a product in the food domain.
 *
 * @param id Unique identifier for the product.
 * @param name Name of the product.
 * @param brand Brand of the product, if available.
 * @param barcode Barcode of the product, if available.
 * @param note Additional note about the product, if available.
 * @param isLiquid Indicates whether the product is liquid (e.g., juice, milk).
 * @param packageWeight Weight of the product package, if available.
 * @param servingWeight Weight of a single serving of the product, if available.
 * @param source Source of the product.
 * @param nutritionFacts Nutrition facts of the product per 100g or 100ml, depending on whether the
 *   product is solid or liquid.
 */
data class Product(
    override val id: FoodId.Product,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val note: String?,
    val isLiquid: Boolean,
    val packageWeight: Double?,
    override val servingWeight: Double?,
    val source: FoodSource,
    override val nutritionFacts: NutritionFacts,
) : Food {
    override val totalWeight: Double? = packageWeight
}
