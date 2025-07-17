package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.v2

import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct
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
    override val brand: String? = null,
    @SerialName("code")
    override val barcode: String? = null,
    @Serializable(with = FloatSerializer::class)
    @SerialName("product_quantity")
    override val packageWeight: Float? = null,
    @SerialName("product_quantity_unit")
    override val packageQuantityUnit: String? = null,
    @Serializable(with = FloatSerializer::class)
    @SerialName("serving_quantity")
    override val servingWeight: Float? = null,
    @SerialName("serving_quantity_unit")
    override val servingQuantityUnit: String? = null,
    @SerialName("nutriments")
    override val nutritionFacts: OpenFoodFactsNutrients? = null
) : OpenFoodFactsProduct {
    override val url: String? = barcode?.let { "https://world.openfoodfacts.org/product/$it" }
}
