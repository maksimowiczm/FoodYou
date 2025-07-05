package com.maksimowiczm.foodyou.feature.food.domain

data class Product(
    override val id: FoodId.Product,
    val name: String,
    val brand: String?,
    override val nutritionFacts: NutritionFacts,
    val barcode: String?,
    val packageWeight: Float?,
    override val servingWeight: Float?,
    override val note: String?
) : Food {
    override val headline: String = if (brand.isNullOrBlank()) name else "$name ($brand)"
    override val totalWeight: Float? = packageWeight
}
