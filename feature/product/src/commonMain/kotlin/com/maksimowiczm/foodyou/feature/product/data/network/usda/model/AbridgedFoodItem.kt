package com.maksimowiczm.foodyou.feature.product.data.network.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AbridgedFoodItem(
    @SerialName("dataType")
    val dataType: String,
    @SerialName("description")
    val description: String,
    @SerialName("foodNutrients")
    val foodNutrients: List<AbridgedFoodNutrient>,
    @SerialName("brandOwner")
    val brand: String? = null,
    @SerialName("gtinUpc")
    val barcode: String? = null
) {
    fun getNutrient(nutrient: Nutrient) = foodNutrients.firstOrNull { it.number == nutrient.number }
}
