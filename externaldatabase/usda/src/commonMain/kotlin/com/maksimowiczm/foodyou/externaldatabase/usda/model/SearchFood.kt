package com.maksimowiczm.foodyou.externaldatabase.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchFood(
    @SerialName("fdcId") override val fdcId: Int,
    @SerialName("description") override val description: String,
    @SerialName("brandOwner") override val brand: String? = null,
    @SerialName("gtinUpc") override val barcode: String? = null,
    @SerialName("servingSize") override val servingSize: Double? = null,
    @SerialName("servingSizeUnit") override val servingSizeUnit: String? = null,
    @SerialName("packageWeight") override val packageWeight: String? = null,
    @SerialName("foodNutrients") override val foodNutrients: List<SearchFoodNutrient>,
) : Food

@Serializable
internal data class SearchFoodNutrient(
    @SerialName("nutrientNumber") override val number: String,
    @SerialName("nutrientName") override val name: String,
    @SerialName("value") override val amount: Double,
    @SerialName("unitName") override val unit: String,
) : FoodNutrient
