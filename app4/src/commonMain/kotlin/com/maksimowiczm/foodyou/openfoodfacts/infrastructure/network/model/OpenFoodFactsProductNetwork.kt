package com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put

@Serializable(with = OpenFoodFactsProductNetworkSerializer::class)
internal data class OpenFoodFactsProductNetwork(
    val code: String,
    val localizedNames: Map<String, String> = emptyMap(),
    val localizedGenericNames: Map<String, String> = emptyMap(),
    val brands: List<String>? = null,
    val brandsTags: List<String>? = null,
    val categories: String? = null,
    val categoriesTags: List<String>? = null,
    val labels: String? = null,
    val labelsTags: List<String>? = null,
    val countriesTags: List<String>? = null,
    val originsTags: List<String>? = null,
    val stores: List<String>? = null,
    val embCodes: List<String>? = null,
    val lang: String? = null,
    val lc: List<String>? = null,
    val quantity: String? = null,
    val servingSize: String? = null,
    val statesTags: List<String>? = null,
    val ingredientsTags: List<String>? = null,
    val allergensTags: List<String>? = null,
    val ingredientsAnalysisTags: List<String>? = null,
    val ingredientsN: String? = null,
    val unknownIngredientsN: String? = null,
    val additivesN: Int? = null,
    val uniqueScansN: Int? = null,
    val scansN: Int? = null,
    val popularityKey: Long? = null,
    val completeness: Double? = null,
    val lastModifiedT: Long? = null,
    val createdT: Long? = null,
    val lastIndexedDatetime: String? = null,
    val nutritionGrades: String? = null,
    val nutriscoreGrade: String? = null,
    val nutriscoreScore: Int? = null,
    val ecoscoreGrade: String? = null,
    val ecoscoreScore: Int? = null,
    val novaGroups: String? = null,
    val novaGroup: Int? = null,
    val nutriments: Nutriments? = null,
    val nutriscoreData: NutriscoreData? = null,
    val imageFrontUrl: String? = null,
    val imageFrontSmallUrl: String? = null,
    val imageFrontThumbUrl: String? = null,
    val imageUrl: String? = null,
    val imageSmallUrl: String? = null,
    val imageThumbUrl: String? = null,
    val score: Double? = null,
)

@Serializable
internal data class Nutriments(
    // Energy
    @SerialName("energy-kcal_100g") val energyKcal100g: Double? = null,
    @SerialName("energy-kj_100g") val energyKj100g: Double? = null,
    // Macronutrients
    @SerialName("proteins_100g") val proteins100g: Double? = null,
    @SerialName("proteins_unit") val proteinsUnit: String? = null,
    @SerialName("carbohydrates_100g") val carbohydrates100g: Double? = null,
    @SerialName("fat_100g") val fat100g: Double? = null,
    @SerialName("saturated-fat_100g") val saturatedFat100g: Double? = null,
    @SerialName("trans-fat_100g") val transFat100g: Double? = null,
    @SerialName("monounsaturated-fat_100g") val monounsaturatedFat100g: Double? = null,
    @SerialName("polyunsaturated-fat_100g") val polyunsaturatedFat100g: Double? = null,
    @SerialName("omega-3-fat_100g") val omega3Fat100g: Double? = null,
    @SerialName("omega-6-fat_100g") val omega6Fat100g: Double? = null,
    @SerialName("sugars_100g") val sugars100g: Double? = null,
    @SerialName("added-sugars_100g") val addedSugars100g: Double? = null,
    @SerialName("salt_100g") val salt100g: Double? = null,
    @SerialName("sodium_100g") val sodium100g: Double? = null,
    @SerialName("fiber_100g") val fiber100g: Double? = null,
    @SerialName("soluble-fiber_100g") val solubleFiber100g: Double? = null,
    @SerialName("insoluble-fiber_100g") val insolubleFiber100g: Double? = null,
    @SerialName("cholesterol_100g") val cholesterol100g: Double? = null,
    @SerialName("caffeine_100g") val caffeine100g: Double? = null,
    // Vitamins
    @SerialName("vitamin-a_100g") val vitaminA100g: Double? = null,
    @SerialName("vitamin-b1_100g") val vitaminB1100g: Double? = null,
    @SerialName("vitamin-b2_100g") val vitaminB2100g: Double? = null,
    @SerialName("vitamin-pp_100g") val vitaminB3100g: Double? = null,
    @SerialName("pantothenic-acid_100g") val vitaminB5100g: Double? = null,
    @SerialName("vitamin-b6_100g") val vitaminB6100g: Double? = null,
    @SerialName("biotin_100g") val vitaminB7100g: Double? = null,
    @SerialName("vitamin-b9_100g") val vitaminB9100g: Double? = null,
    @SerialName("vitamin-b12_100g") val vitaminB12100g: Double? = null,
    @SerialName("vitamin-c_100g") val vitaminC100g: Double? = null,
    @SerialName("vitamin-d_100g") val vitaminD100g: Double? = null,
    @SerialName("vitamin-e_100g") val vitaminE100g: Double? = null,
    @SerialName("vitamin-k_100g") val vitaminK100g: Double? = null,
    // Minerals
    @SerialName("manganese_100g") val manganese100g: Double? = null,
    @SerialName("magnesium_100g") val magnesium100g: Double? = null,
    @SerialName("potassium_100g") val potassium100g: Double? = null,
    @SerialName("calcium_100g") val calcium100g: Double? = null,
    @SerialName("copper_100g") val copper100g: Double? = null,
    @SerialName("zinc_100g") val zinc100g: Double? = null,
    @SerialName("iron_100g") val iron100g: Double? = null,
    @SerialName("phosphorus_100g") val phosphorus100g: Double? = null,
    @SerialName("selenium_100g") val selenium100g: Double? = null,
    @SerialName("iodine_100g") val iodine100g: Double? = null,
    @SerialName("chromium_100g") val chromium100g: Double? = null,
)

@Serializable
internal data class NutriscoreData(
    @SerialName("energy") val energy: Double? = null,
    @SerialName("energy_points") val energyPoints: Int? = null,
    @SerialName("energy_value") val energyValue: Double? = null,
    @SerialName("fiber") val fiber: Double? = null,
    @SerialName("fiber_points") val fiberPoints: Int? = null,
    @SerialName("fiber_value") val fiberValue: Double? = null,
    @SerialName("fruits_vegetables_nuts_colza_walnut_olive_oils")
    val fruitsVegetablesNutsColzaWalnutOliveOils: Double? = null,
    @SerialName("fruits_vegetables_nuts_colza_walnut_olive_oils_points")
    val fruitsVegetablesNutsColzaWalnutOliveOilsPoints: Int? = null,
    @SerialName("fruits_vegetables_nuts_colza_walnut_olive_oils_value")
    val fruitsVegetablesNutsColzaWalnutOliveOilsValue: Double? = null,
    @SerialName("grade") val grade: String? = null,
    @SerialName("is_beverage") val isBeverage: Int? = null,
    @Serializable(with = FlexibleIntSerializer::class)
    @SerialName("is_cheese")
    val isCheese: Int? = null,
    @SerialName("is_fat") val isFat: Int? = null,
    @SerialName("is_water") val isWater: Int? = null,
    @SerialName("is_fat_oil_nuts_seeds") val isFatOilNutsSeeds: Int? = null,
    @SerialName("is_red_meat_product") val isRedMeatProduct: Int? = null,
    @SerialName("negative_points") val negativePoints: Int? = null,
    @SerialName("negative_points_max") val negativePointsMax: Int? = null,
    @SerialName("positive_points") val positivePoints: Int? = null,
    @SerialName("positive_points_max") val positivePointsMax: Int? = null,
    @SerialName("positive_nutrients") val positiveNutrients: List<String>? = null,
    @SerialName("proteins") val proteins: Double? = null,
    @SerialName("proteins_points") val proteinsPoints: Int? = null,
    @SerialName("proteins_value") val proteinsValue: Double? = null,
    @SerialName("saturated_fat") val saturatedFat: Double? = null,
    @SerialName("saturated_fat_points") val saturatedFatPoints: Int? = null,
    @SerialName("saturated_fat_value") val saturatedFatValue: Double? = null,
    @SerialName("score") val score: Int? = null,
    @SerialName("sodium") val sodium: Double? = null,
    @SerialName("sodium_points") val sodiumPoints: Int? = null,
    @SerialName("sodium_value") val sodiumValue: Double? = null,
    @SerialName("sugars") val sugars: Double? = null,
    @SerialName("sugars_points") val sugarsPoints: Int? = null,
    @SerialName("sugars_value") val sugarsValue: Double? = null,
    @SerialName("count_proteins") val countProteins: Int? = null,
    @SerialName("count_proteins_reason") val countProteinsReason: String? = null,
    @SerialName("components") val components: NutriscoreComponents? = null,
)

@Serializable
internal data class NutriscoreComponents(
    @SerialName("negative") val negative: List<NutrientComponent>? = null,
    @SerialName("positive") val positive: List<NutrientComponent>? = null,
)

@Serializable
internal data class NutrientComponent(
    @SerialName("id") val id: String,
    @SerialName("points") val points: Int,
    @SerialName("points_max") val pointsMax: Int,
    @SerialName("unit") val unit: String,
    @SerialName("value") val value: Double? = null,
)

internal object OpenFoodFactsProductNetworkSerializer : KSerializer<OpenFoodFactsProductNetwork> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenFoodFactsProduct")

    fun arrayOrLiteral(element: JsonElement?): List<String>? =
        when (element) {
            is JsonArray -> element.mapNotNull { it.jsonPrimitive.contentOrNull }
            is JsonPrimitive -> element.contentOrNull?.split(",")
            else -> emptyList()
        }

    fun JsonObjectBuilder.putStringList(key: String, list: List<String>?) {
        list?.let { put(key, JsonArray(it.map(::JsonPrimitive))) }
    }

    override fun deserialize(decoder: Decoder): OpenFoodFactsProductNetwork {
        require(decoder is JsonDecoder) { "This serializer only works with JSON" }

        val element = decoder.decodeJsonElement().jsonObject
        val json = decoder.json

        // Extract localized names from all product_name_* and generic_name_* fields
        val localizedNames = mutableMapOf<String, String>()
        val localizedGenericNames = mutableMapOf<String, String>()

        element.forEach { (key, value) ->
            when {
                key.startsWith("product_name_") && value is JsonPrimitive -> {
                    val locale = key.removePrefix("product_name_")
                    value.contentOrNull?.let {
                        if (it.isNotBlank()) {
                            localizedNames[locale] = it
                        }
                    }
                }

                key.startsWith("generic_name_") && value is JsonPrimitive -> {
                    val locale = key.removePrefix("generic_name_")
                    value.contentOrNull?.let {
                        if (it.isNotBlank()) {
                            localizedGenericNames[locale] = it
                        }
                    }
                }

                key == "product_name" && value is JsonPrimitive -> {
                    value.contentOrNull?.let {
                        if (it.isNotBlank()) {
                            localizedNames[""] = it
                        }
                    }
                }

                key == "generic_name" && value is JsonPrimitive -> {
                    value.contentOrNull?.let {
                        if (it.isNotBlank()) {
                            localizedGenericNames[""] = it
                        }
                    }
                }
            }
        }

        return OpenFoodFactsProductNetwork(
            code = element["code"]?.jsonPrimitive?.content ?: "",
            localizedNames = localizedNames,
            localizedGenericNames = localizedGenericNames,
            brands = arrayOrLiteral(element["brands"]),
            brandsTags = arrayOrLiteral(element["brands_tags"]),
            categories = element["categories"]?.jsonPrimitive?.contentOrNull,
            categoriesTags = arrayOrLiteral(element["categories_tags"]),
            labels = element["labels"]?.jsonPrimitive?.contentOrNull,
            labelsTags = arrayOrLiteral(element["labels_tags"]),
            countriesTags = arrayOrLiteral(element["countries_tags"]),
            originsTags = arrayOrLiteral(element["origins_tags"]),
            stores = arrayOrLiteral(element["stores"]),
            embCodes = arrayOrLiteral(element["emb_codes"]),
            lang = element["lang"]?.jsonPrimitive?.contentOrNull,
            lc = arrayOrLiteral(element["lc"]),
            quantity = element["quantity"]?.jsonPrimitive?.contentOrNull,
            servingSize = element["serving_size"]?.jsonPrimitive?.contentOrNull,
            statesTags = arrayOrLiteral(element["states_tags"]),
            ingredientsTags = arrayOrLiteral(element["ingredients_tags"]),
            allergensTags = arrayOrLiteral(element["allergens_tags"]),
            ingredientsAnalysisTags = arrayOrLiteral(element["ingredients_analysis_tags"]),
            ingredientsN = element["ingredients_n"]?.jsonPrimitive?.contentOrNull,
            unknownIngredientsN = element["unknown_ingredients_n"]?.jsonPrimitive?.contentOrNull,
            additivesN = element["additives_n"]?.jsonPrimitive?.intOrNull,
            uniqueScansN = element["unique_scans_n"]?.jsonPrimitive?.intOrNull,
            scansN = element["scans_n"]?.jsonPrimitive?.intOrNull,
            popularityKey = element["popularity_key"]?.jsonPrimitive?.longOrNull,
            completeness = element["completeness"]?.jsonPrimitive?.doubleOrNull,
            lastModifiedT = element["last_modified_t"]?.jsonPrimitive?.longOrNull,
            createdT = element["created_t"]?.jsonPrimitive?.longOrNull,
            lastIndexedDatetime = element["last_indexed_datetime"]?.jsonPrimitive?.contentOrNull,
            nutritionGrades = element["nutrition_grades"]?.jsonPrimitive?.contentOrNull,
            nutriscoreGrade = element["nutriscore_grade"]?.jsonPrimitive?.contentOrNull,
            nutriscoreScore = element["nutriscore_score"]?.jsonPrimitive?.intOrNull,
            ecoscoreGrade = element["ecoscore_grade"]?.jsonPrimitive?.contentOrNull,
            ecoscoreScore = element["ecoscore_score"]?.jsonPrimitive?.intOrNull,
            novaGroups = element["nova_groups"]?.jsonPrimitive?.contentOrNull,
            novaGroup = element["nova_group"]?.jsonPrimitive?.intOrNull,
            nutriments = element["nutriments"]?.let { json.decodeFromJsonElement(it) },
            nutriscoreData = element["nutriscore_data"]?.let { json.decodeFromJsonElement(it) },
            imageFrontUrl = element["image_front_url"]?.jsonPrimitive?.contentOrNull,
            imageFrontSmallUrl = element["image_front_small_url"]?.jsonPrimitive?.contentOrNull,
            imageFrontThumbUrl = element["image_front_thumb_url"]?.jsonPrimitive?.contentOrNull,
            imageUrl = element["image_url"]?.jsonPrimitive?.contentOrNull,
            imageSmallUrl = element["image_small_url"]?.jsonPrimitive?.contentOrNull,
            imageThumbUrl = element["image_thumb_url"]?.jsonPrimitive?.contentOrNull,
            score = element["score"]?.jsonPrimitive?.doubleOrNull,
        )
    }

    override fun serialize(encoder: Encoder, value: OpenFoodFactsProductNetwork) {
        require(encoder is JsonEncoder) { "This serializer only works with JSON" }

        encoder.encodeJsonElement(
            buildJsonObject {
                put("code", value.code)

                // Expand localized names into individual fields
                value.localizedNames.forEach { (locale, name) -> put("product_name_$locale", name) }
                value.localizedGenericNames.forEach { (locale, name) ->
                    put("generic_name_$locale", name)
                }

                putStringList("brands", value.brands)
                putStringList("brands_tags", value.brandsTags)
                putStringList("categories_tags", value.categoriesTags)
                putStringList("labels_tags", value.labelsTags)
                putStringList("countries_tags", value.countriesTags)
                putStringList("origins_tags", value.originsTags)
                putStringList("stores", value.stores)
                putStringList("emb_codes", value.embCodes)
                putStringList("states_tags", value.statesTags)
                putStringList("ingredients_tags", value.ingredientsTags)
                putStringList("allergens_tags", value.allergensTags)
                putStringList("ingredients_analysis_tags", value.ingredientsAnalysisTags)
                putStringList("lc", value.lc)

                value.categories?.let { put("categories", it) }
                value.labels?.let { put("labels", it) }
                value.lang?.let { put("lang", it) }
                value.quantity?.let { put("quantity", it) }
                value.servingSize?.let { put("serving_size", it) }
                value.ingredientsN?.let { put("ingredients_n", it) }
                value.unknownIngredientsN?.let { put("unknown_ingredients_n", it) }
                value.additivesN?.let { put("additives_n", it) }
                value.uniqueScansN?.let { put("unique_scans_n", it) }
                value.scansN?.let { put("scans_n", it) }
                value.popularityKey?.let { put("popularity_key", it) }
                value.completeness?.let { put("completeness", it) }
                value.lastModifiedT?.let { put("last_modified_t", it) }
                value.createdT?.let { put("created_t", it) }
                value.lastIndexedDatetime?.let { put("last_indexed_datetime", it) }
                value.nutritionGrades?.let { put("nutrition_grades", it) }
                value.nutriscoreGrade?.let { put("nutriscore_grade", it) }
                value.nutriscoreScore?.let { put("nutriscore_score", it) }
                value.ecoscoreGrade?.let { put("ecoscore_grade", it) }
                value.ecoscoreScore?.let { put("ecoscore_score", it) }
                value.novaGroups?.let { put("nova_groups", it) }
                value.novaGroup?.let { put("nova_group", it) }
                value.imageFrontUrl?.let { put("image_front_url", it) }
                value.imageFrontSmallUrl?.let { put("image_front_small_url", it) }
                value.imageFrontThumbUrl?.let { put("image_front_thumb_url", it) }
                value.imageUrl?.let { put("image_url", it) }
                value.imageSmallUrl?.let { put("image_small_url", it) }
                value.imageThumbUrl?.let { put("image_thumb_url", it) }
                value.score?.let { put("score", it) }

                value.nutriments?.let { put("nutriments", encoder.json.encodeToJsonElement(it)) }
                value.nutriscoreData?.let {
                    put("nutriscore_data", encoder.json.encodeToJsonElement(it))
                }
            }
        )
    }
}

private object FlexibleIntSerializer : KSerializer<Int?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("FlexibleString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Int? {
        return when (val element = (decoder as? JsonDecoder)?.decodeJsonElement()) {
            is JsonPrimitive -> element.content.toIntOrNull()
            else -> decoder.decodeInt()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Int?) {
        if (value != null) encoder.encodeInt(value) else encoder.encodeNull()
    }
}
