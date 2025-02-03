package com.maksimowiczm.foodyou.core.feature.product.data.model

import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity
import com.maksimowiczm.foodyou.core.feature.product.network.openfoodfacts.model.OpenFoodProduct

data class Product(
    val id: Long,

    val name: String,
    val brand: String? = null,
    val barcode: String? = null,

    /**
     * Amount of calories per 100 grams of the product.
     */
    val calories: Float,

    /**
     * Nutritional values of the product per 100 grams.
     */
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: Float? = null,
    val fats: Float,
    val saturatedFats: Float? = null,
    val salt: Float? = null,
    val sodium: Float? = null,
    val fiber: Float? = null,

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
) {
    fun calories(amount: Float) = calories * amount / 100

    fun protein(amount: Float) = proteins * amount / 100

    fun carbohydrates(amount: Float) = carbohydrates * amount / 100

    fun fats(amount: Float) = fats * amount / 100
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        brand = brand,
        barcode = barcode,
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        sugars = sugars,
        fats = fats,
        saturatedFats = saturatedFats,
        salt = salt,
        sodium = sodium,
        fiber = fiber,
        packageWeight = packageWeight,
        servingWeight = servingWeight,
        weightUnit = weightUnit,
        productSource = productSource
    )
}

/**
 * Converts an [OpenFoodProduct] to a [Product]. Returns null if the conversion is not possible.
 */
internal fun OpenFoodProduct.toEntity(): ProductEntity? {
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

    if (listOf(
            nutriments.energy100g,
            nutriments.proteins100g,
            nutriments.carbohydrates100g,
            nutriments.fat100g,
            code
        ).any { it == null }
    ) {
        return null
    }

    return ProductEntity(
        name = productName,
        brand = brands,
        barcode = code,
        calories = nutriments.energy100g!!,
        proteins = nutriments.proteins100g!!,
        carbohydrates = nutriments.carbohydrates100g!!,
        sugars = nutriments.sugars100g,
        fats = nutriments.fat100g!!,
        saturatedFats = nutriments.saturatedFat100g,
        salt = nutriments.salt100g,
        sodium = nutriments.sodium100g,
        fiber = nutriments.fiber100g,
        packageWeight = packageQuantity,
        servingWeight = servingQuantity,
        weightUnit = weightUnit,
        productSource = ProductSource.OpenFoodFacts
    )
}

private fun String.toWeightUnit(): WeightUnit? {
    return when (this) {
        "g" -> WeightUnit.Gram
        "ml" -> WeightUnit.Milliliter
        else -> null
    }
}
