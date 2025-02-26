package com.maksimowiczm.foodyou.data.model

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
