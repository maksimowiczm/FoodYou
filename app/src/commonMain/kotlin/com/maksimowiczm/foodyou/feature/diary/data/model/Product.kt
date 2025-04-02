package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.data.NutrientsHelper
import com.maksimowiczm.foodyou.feature.diary.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsProduct

data class Product(
    val id: Long,

    val name: String,
    val brand: String? = null,
    val barcode: String? = null,

    val nutrients: Nutrients,

    /**
     * Amount of the product in the package in grams.
     */
    val packageWeight: Float? = null,

    /**
     * Unit of the product serving in grams.
     */
    val servingWeight: Float? = null,

    /**
     * Unit of the product quantity.
     */
    val weightUnit: WeightUnit,

    /**
     * Source of the product data.
     */
    val productSource: ProductSource
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    brand = brand,
    barcode = barcode,
    nutrients = Nutrients(
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        sugars = sugars,
        fats = fats,
        saturatedFats = saturatedFats,
        salt = salt,
        sodium = sodium,
        fiber = fiber
    ),
    packageWeight = packageWeight,
    servingWeight = servingWeight,
    weightUnit = weightUnit,
    productSource = productSource
)

/**
 * Converts an [OpenFoodFactsProduct] to a [Product]. Returns null if the conversion is not possible.
 */
internal fun OpenFoodFactsProduct.toEntity(): ProductEntity? {
    // 1. Validate all required fields

    val nutrients = nutrients ?: return null
    val productName = productName ?: return null

    val packageQuantityUnit = packageQuantityUnit?.toWeightUnit()
    val servingQuantityUnit = servingQuantityUnit?.toWeightUnit()

    if (
        packageQuantityUnit != null &&
        servingQuantityUnit != null &&
        packageQuantityUnit != servingQuantityUnit
    ) {
        return null
    }

    val weightUnit = packageQuantityUnit ?: WeightUnit.Gram

    if (
        nutrients.proteins100g == null ||
        nutrients.carbohydrates100g == null ||
        nutrients.fat100g == null ||
        code == null
    ) {
        return null
    }

    // 2. Sometimes food doesn't have energy100g but it is trivial to calculate it from other values
    // (proteins, carbohydrates, fats)
    val energy100g = nutrients.energy100g ?: NutrientsHelper.calculateCalories(
        proteins = nutrients.proteins100g,
        carbohydrates = nutrients.carbohydrates100g,
        fats = nutrients.fat100g
    )

    return ProductEntity(
        name = productName,
        brand = brands,
        barcode = code,
        calories = energy100g,
        proteins = nutrients.proteins100g,
        carbohydrates = nutrients.carbohydrates100g,
        sugars = nutrients.sugars100g,
        fats = nutrients.fat100g,
        saturatedFats = nutrients.saturatedFat100g,
        salt = nutrients.salt100g,
        sodium = nutrients.sodium100g,
        fiber = nutrients.fiber100g,
        packageWeight = packageQuantity,
        servingWeight = servingQuantity,
        weightUnit = weightUnit,
        productSource = ProductSource.OpenFoodFacts
    )
}

private fun String.toWeightUnit(): WeightUnit? = when (this) {
    "g" -> WeightUnit.Gram
    "ml" -> WeightUnit.Milliliter
    else -> null
}
