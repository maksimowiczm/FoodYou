package com.maksimowiczm.foodyou.core.model

data class Product(
    override val id: FoodId.Product,
    val name: String,
    val brand: String?,
    override val nutritionFacts: NutritionFacts,
    val barcode: String?,
    override val totalWeight: Float?,
    override val servingWeight: Float?
) : Food {
    override val headline: String
        get() = if (brand.isNullOrBlank()) name else "$name ($brand)"
}
