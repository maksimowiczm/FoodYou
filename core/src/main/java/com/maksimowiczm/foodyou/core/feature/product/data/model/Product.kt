package com.maksimowiczm.foodyou.core.feature.product.data.model

import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity

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
