package com.maksimowiczm.foodyou.feature.diary.network.model.v2

import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.feature.diary.network.model.OpenFoodFactsProduct
import kotlin.Float
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.serializer

private object FloatSerializer : JsonTransformingSerializer<Float>(serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonPrimitive && element.isString) {
            JsonPrimitive(element.content.toFloatOrNull())
        } else {
            element
        }
}

@Serializable
internal data class OpenFoodFactsProductV2(
    @SerialName("product_name")
    override val productName: String,
    @SerialName("brands")
    override val brands: String? = null,
    @SerialName("code")
    override val code: String? = null,
    @SerialName("image_url")
    override val imageUrl: String? = null,
    @SerialName("nutriments")
    override val nutrients: OpenFoodFactsNutrients,
    @Serializable(with = FloatSerializer::class)
    @SerialName("product_quantity")
    override val packageQuantity: Float? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @Serializable(with = FloatSerializer::class)
    @SerialName("serving_quantity")
    override val servingQuantity: Float? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null
) : OpenFoodFactsProduct
