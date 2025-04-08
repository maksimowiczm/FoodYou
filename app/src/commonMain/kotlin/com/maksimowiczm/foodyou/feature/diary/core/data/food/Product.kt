package com.maksimowiczm.foodyou.feature.diary.core.data.food

/**
 * Represents a food product.
 *
 * @property id The unique identifier of the product.
 * @property name The name of the product.
 * @property brand The brand of the product, if available.
 * @property nutrients The nutritional information of the product per 100g.
 * @property barcode The barcode of the product, if available.
 * @property packageWeight The weight of the product packaging, if available.
 * @property servingWeight The weight of a serving of the product, if available.
 */
data class Product(
    override val id: FoodId.Product,
    override val name: String,
    override val brand: String?,
    override val nutrients: Nutrients,
    val barcode: String?,
    override val packageWeight: PortionWeight.Package?,
    override val servingWeight: PortionWeight.Serving?
) : Food
