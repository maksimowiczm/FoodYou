package com.maksimowiczm.foodyou.feature.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NullableAbridgedFoodItem(
    @SerialName("dataType")
    val dataType: String,
    @SerialName("description")
    override val description: String,
    @SerialName("foodNutrients")
    val nullableFoodNutrients: List<NullableAbridgedFoodNutrient>,
    @SerialName("brandOwner")
    override val brand: String? = null,
    @SerialName("gtinUpc")
    override val barcode: String? = null
) : AbridgedFoodItem {

    override val foodNutrients: List<AbridgedFoodNutrient>
        get() = nullableFoodNutrients.mapNotNull { nutrient ->
            if (nutrient.number == null ||
                nutrient.name == null ||
                nutrient.amount == null ||
                nutrient.unit == null
            ) {
                return@mapNotNull null
            }

            AbridgedFoodNutrient(
                number = nutrient.number,
                name = nutrient.name,
                amount = nutrient.amount,
                unit = nutrient.unit
            )
        }
}
