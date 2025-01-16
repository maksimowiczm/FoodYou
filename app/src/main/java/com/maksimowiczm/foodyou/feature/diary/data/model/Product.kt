package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.database.ProductEntity

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
    val packageQuantity: Float? = null,

    /**
     * Unit of the product serving in grams.
     */
    val servingQuantity: Float? = null,

    /**
     * Unit of the product quantity.
     */
    val weightUnit: WeightUnit
)

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        name = name,
        brand = brand,
        barcode = barcode?.value,
        calories = calories,
        proteins = proteins,
        carbohydrates = carbohydrates,
        sugars = sugars,
        fats = fats,
        saturatedFats = saturatedFats,
        salt = salt,
        sodium = sodium,
        fiber = fiber,
        packageQuantity = packageQuantity,
        servingQuantity = servingQuantity,
        weightUnit = weightUnit
    )
}
