package com.maksimowiczm.foodyou.feature.product.data.network.openfoodfacts.model.v2

import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
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
    override val name: String? = null,
    @SerialName("brands")
    val brands: String? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("nutriments")
    val nutrients: OpenFoodFactsNutrients? = null,
    @Serializable(with = FloatSerializer::class)
    @SerialName("product_quantity")
    val packageQuantity: Float? = null,
    @SerialName("product_quantity_unit")
    val packageQuantityUnit: String? = null,
    @Serializable(with = FloatSerializer::class)
    @SerialName("serving_quantity")
    val servingQuantity: Float? = null,
    @SerialName("serving_quantity_unit")
    val servingQuantityUnit: String? = null
) : RemoteProduct
