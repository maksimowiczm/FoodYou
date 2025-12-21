package com.maksimowiczm.foodyou.food.infrastructure.usda.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.serializer

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
    @Serializable(with = IntSerializer::class) @SerialName("number") val number: Int? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("amount") val amount: Double? = null,
    @SerialName("unitName") val unitName: String? = null,
)

private object IntSerializer : JsonTransformingSerializer<Int?>(serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonPrimitive && element.isString)
            JsonPrimitive(element.content.toIntOrNull())
        else element
}
