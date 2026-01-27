package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.v2

import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model.OpenFoodFactsProduct
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = OpenFoodFactsProductV2Serializer::class)
internal data class OpenFoodFactsProductV2(
    override val localizedNames: Map<String, String> = emptyMap(),
    override val brand: String?,
    override val barcode: String,
    override val packageWeight: Double?,
    override val packageQuantityUnit: String?,
    override val servingWeight: Double?,
    override val servingQuantityUnit: String?,
    override val nutritionFacts: OpenFoodFactsNutrients?,
    override val thumbnailUrl: String?,
    override val imageUrl: String?,
) : OpenFoodFactsProduct {
    override val url: String = "https://world.openfoodfacts.org/product/$barcode"
}

internal object OpenFoodFactsProductV2Serializer : KSerializer<OpenFoodFactsProductV2> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenFoodFactsProductV2")

    override fun deserialize(decoder: Decoder): OpenFoodFactsProductV2 {
        require(decoder is JsonDecoder) { "This serializer only works with JSON" }

        val jsonObject = decoder.decodeJsonElement().jsonObject

        // Extract localized names from all product_name_* fields
        val localizedNames = mutableMapOf<String, String>()
        jsonObject.forEach { (key, value) ->
            if (key.startsWith("product_name_") && value is JsonPrimitive) {
                val locale = key.removePrefix("product_name_")
                value.contentOrNull?.let { localizedNames[locale] = it }
            }
        }

        // Deserialize other fields
        val json = decoder.json

        return OpenFoodFactsProductV2(
            localizedNames = localizedNames,
            brand = jsonObject["brands"]?.jsonPrimitive?.contentOrNull,
            barcode =
                jsonObject["code"]?.jsonPrimitive?.content ?: error("Missing required field: code"),
            packageWeight =
                jsonObject["product_quantity"]?.jsonPrimitive?.let {
                    it.doubleOrNull ?: it.contentOrNull?.toDoubleOrNull()
                },
            packageQuantityUnit = jsonObject["product_quantity_unit"]?.jsonPrimitive?.contentOrNull,
            servingWeight =
                jsonObject["serving_quantity"]?.jsonPrimitive?.let {
                    it.doubleOrNull ?: it.contentOrNull?.toDoubleOrNull()
                },
            servingQuantityUnit = jsonObject["serving_quantity_unit"]?.jsonPrimitive?.contentOrNull,
            nutritionFacts =
                jsonObject["nutriments"]?.let {
                    json.decodeFromJsonElement<OpenFoodFactsNutrients>(it)
                },
            thumbnailUrl = jsonObject["image_thumb_url"]?.jsonPrimitive?.contentOrNull,
            imageUrl = jsonObject["image_url"]?.jsonPrimitive?.contentOrNull,
        )
    }

    override fun serialize(encoder: Encoder, value: OpenFoodFactsProductV2) =
        error("Serialization is not supported for OpenFoodFactsProductV2")
}
