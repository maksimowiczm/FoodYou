package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.v1

import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.model.OpenFoodFactsProduct
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = OpenFoodFactsProductV1Serializer::class)
data class OpenFoodFactsProductV1(
    override val localizedNames: Map<String, String> = emptyMap(),
    override val brand: String?,
    override val barcode: String,
    override val nutritionFacts: OpenFoodFactsNutrients,
    override val packageWeight: Double?,
    override val packageQuantityUnit: String?,
    override val servingWeight: Double?,
    override val servingQuantityUnit: String?,
    override val url: String?,
    override val thumbnailUrl: String?,
    override val imageUrl: String?,
) : OpenFoodFactsProduct

object OpenFoodFactsProductV1Serializer : KSerializer<OpenFoodFactsProductV1> {
    private val json = Json { ignoreUnknownKeys = true }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenFoodFactsProductV1")

    override fun deserialize(decoder: Decoder): OpenFoodFactsProductV1 {
        val jsonElement = decoder.decodeSerializableValue(JsonElement.serializer())
        val jsonObject = jsonElement.jsonObject

        // Extract localized names
        val localizedNames = mutableMapOf<String, String>()
        jsonObject.forEach { (key, value) ->
            if (key.startsWith("product_name_") && value is JsonPrimitive) {
                val locale = key.removePrefix("product_name_")
                value.contentOrNull?.let { localizedNames[locale] = it }
            }
        }

        // Deserialize other fields normally
        return OpenFoodFactsProductV1(
            localizedNames = localizedNames,
            brand = jsonObject["brands"]?.jsonPrimitive?.contentOrNull,
            barcode = jsonObject["code"]?.jsonPrimitive?.content ?: "",
            nutritionFacts = json.decodeFromJsonElement(jsonObject["nutriments"]!!),
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
            url = jsonObject["url"]?.jsonPrimitive?.contentOrNull,
            thumbnailUrl = jsonObject["image_thumb_url"]?.jsonPrimitive?.contentOrNull,
            imageUrl = jsonObject["image_url"]?.jsonPrimitive?.contentOrNull,
        )
    }

    override fun serialize(encoder: Encoder, value: OpenFoodFactsProductV1) =
        error("Serialization is not supported")
}
