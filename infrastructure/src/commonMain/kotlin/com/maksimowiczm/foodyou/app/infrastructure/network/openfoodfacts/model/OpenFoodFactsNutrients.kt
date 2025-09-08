package com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsNutrients(
    @SerialName("energy-kcal_100g") val energy: Double? = null,
    @SerialName("proteins_100g") val proteins: Double? = null,
    @SerialName("proteins_unit") val proteinsUnit: String? = null,
    @SerialName("carbohydrates_100g") val carbohydrates: Double? = null,
    @SerialName("fat_100g") val fats: Double? = null,
    @SerialName("saturated-fat_100g") val saturatedFats: Double? = null,
    @SerialName("trans-fat_100") val transFats: Double? = null,
    @SerialName("monounsaturated-fat_100g") val monounsaturatedFats: Double? = null,
    @SerialName("polyunsaturated-fat_100g") val polyunsaturatedFats: Double? = null,
    @SerialName("omega-3-fat_100g") val omega3Fats: Double? = null,
    @SerialName("omega-6-fat_100g") val omega6Fats: Double? = null,
    @SerialName("sugars_100g") val sugars: Double? = null,
    @SerialName("added-sugars_100g") val addedSugars: Double? = null,
    @SerialName("salt_100g") val salt: Double? = null,
    @SerialName("fiber_100g") val fiber: Double? = null,
    @SerialName("soluble-fiber_100g") val solubleFiber: Double? = null,
    @SerialName("insoluble-fiber_100g") val insolubleFiber: Double? = null,
    @SerialName("cholesterol_100g") val cholesterol: Double? = null,
    @SerialName("caffeine_100g") val caffeine: Double? = null,
    // Vitamins
    @SerialName("vitamin-a_100g") val vitaminA: Double? = null,
    @SerialName("vitamin-b1_100g") val vitaminB1: Double? = null,
    @SerialName("vitamin-b2_100g") val vitaminB2: Double? = null,
    @SerialName("vitamin-pp_100g") val vitaminB3: Double? = null,
    @SerialName("pantothenic-acid_100g") val vitaminB5: Double? = null,
    @SerialName("vitamin-b6_100g") val vitaminB6: Double? = null,
    @SerialName("biotin_100g") val vitaminB7: Double? = null,
    @SerialName("vitamin-b9_100g") val vitaminB9: Double? = null,
    @SerialName("vitamin-b12_100g") val vitaminB12: Double? = null,
    @SerialName("vitamin-c_100g") val vitaminC: Double? = null,
    @SerialName("vitamin-d_100g") val vitaminD: Double? = null,
    @SerialName("vitamin-e_100g") val vitaminE: Double? = null,
    @SerialName("vitamin-k_100g") val vitaminK: Double? = null,
    // Minerals
    @SerialName("manganese_100g") val manganese: Double? = null,
    @SerialName("magnesium_100g") val magnesium: Double? = null,
    @SerialName("potassium_100g") val potassium: Double? = null,
    @SerialName("calcium_100g") val calcium: Double? = null,
    @SerialName("copper_100g") val copper: Double? = null,
    @SerialName("zinc_100g") val zinc: Double? = null,
    @SerialName("sodium_100g") val sodium: Double? = null,
    @SerialName("iron_100g") val iron: Double? = null,
    @SerialName("phosphorus_100g") val phosphorus: Double? = null,
    @SerialName("selenium_100g") val selenium: Double? = null,
    @SerialName("iodine_100g") val iodine: Double? = null,
    @SerialName("chromium_100g") val chromium: Double? = null,
)
