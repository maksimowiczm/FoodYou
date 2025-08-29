package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DetailedFood(
    @SerialName("fdcId") override val fdcId: Int,
    @SerialName("description") override val description: String,
    @SerialName("brandOwner") override val brand: String? = null,
    @SerialName("gtinUpc") override val barcode: String? = null,
    @SerialName("servingSize") override val servingSize: Double? = null,
    @SerialName("servingSizeUnit") override val servingSizeUnit: String? = null,
    @SerialName("packageWeight") override val packageWeight: String? = null,
    @SerialName("foodNutrients") override val foodNutrients: List<DetailedFoodNutrient>,
) : Food

@Serializable
internal data class DetailedFoodNutrient(
    @SerialName("nutrient") val nutrient: DetailedNutrient,
    @SerialName("amount") override val amount: Double,
) : FoodNutrient {
    override val number: String
        get() = nutrient.number

    override val name: String
        get() = nutrient.name

    override val unit: String
        get() = nutrient.unit
}

@Serializable
internal data class DetailedNutrient(
    @SerialName("number") val number: String,
    @SerialName("name") val name: String,
    @SerialName("unitName") val unit: String,
)
