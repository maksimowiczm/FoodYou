package com.maksimowiczm.foodyou.food.infrastructure.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AbridgedFoodItem(
    @SerialName("fdcId") val fdcId: Int,
    @SerialName("dataType") val dataType: String,
    @SerialName("description") val description: String,
    @SerialName("foodNutrients") val foodNutrients: List<AbridgedFoodNutrient> = emptyList(),
    @SerialName("brandOwner") val brandOwner: String? = null,
    @SerialName("gtinUpc") val gtinUpc: String? = null,
)

@Serializable
data class AbridgedFoodNutrient(
    @SerialName("number") val number: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("unitName") val unitName: String? = null,
)
