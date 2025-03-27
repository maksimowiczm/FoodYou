package com.maksimowiczm.foodyou.feature.search.domain.model

data class Product(
    val id: Long,

    val name: String,
    val brand: String?,
    val barcode: String?,

    val nutrients: Nutrients,

    /**
     * Amount of the product in the package in grams.
     */
    val packageWeight: Float?,

    /**
     * Unit of the product serving in grams.
     */
    val servingWeight: Float?
)
