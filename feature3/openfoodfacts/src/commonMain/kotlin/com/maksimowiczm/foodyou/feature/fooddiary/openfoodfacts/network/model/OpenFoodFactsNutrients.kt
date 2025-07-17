package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsNutrients(
    @SerialName("energy-kcal_100g")
    val energy: Double? = null,
    @SerialName("proteins_100g")
    val proteins: Double? = null,
    @SerialName("proteins_unit")
    val proteinsUnit: String? = null,
    @SerialName("carbohydrates_100g")
    val carbohydrates: Double? = null,
    @SerialName("carbohydrates_unit")
    val carbohydratesUnit: String? = null,
    @SerialName("fat_100g")
    val fats: Double? = null,
    @SerialName("fat_unit")
    val fatsUnit: String? = null,
    @SerialName("saturated-fat_100g")
    val saturatedFats: Double? = null,
    @SerialName("saturated-fat_unit")
    val saturatedFatsUnit: String? = null,
    @SerialName("trans-fat_100")
    val transFats: Double? = null,
    @SerialName("trans-fat_unit")
    val transFatsUnit: String? = null,
    @SerialName("monounsaturated-fat_100g")
    val monounsaturatedFats: Double? = null,
    @SerialName("monounsaturated-fat_unit")
    val monounsaturatedFatsUnit: String? = null,
    @SerialName("polyunsaturated-fat_100g")
    val polyunsaturatedFats: Double? = null,
    @SerialName("polyunsaturated-fat_unit")
    val polyunsaturatedFatsUnit: String? = null,
    @SerialName("omega-3-fat_100g")
    val omega3Fats: Double? = null,
    @SerialName("omega-3-fat_unit")
    val omega3FatsUnit: String? = null,
    @SerialName("omega-6-fat_100g")
    val omega6Fats: Double? = null,
    @SerialName("omega-6-fat_unit")
    val omega6FatsUnit: String? = null,
    @SerialName("sugars_100g")
    val sugars: Double? = null,
    @SerialName("sugars_unit")
    val sugarsUnit: String? = null,
    @SerialName("added-sugars_100g")
    val addedSugars: Double? = null,
    @SerialName("added-sugars_unit")
    val addedSugarsUnit: String? = null,
    @SerialName("salt_100g")
    val salt: Double? = null,
    @SerialName("salt_unit")
    val saltUnit: String? = null,
    @SerialName("fiber_100g")
    val fiber: Double? = null,
    @SerialName("fiber_unit")
    val fiberUnit: String? = null,
    @SerialName("soluble-fiber_100g")
    val solubleFiber: Double? = null,
    @SerialName("soluble-fiber_unit")
    val solubleFiberUnit: String? = null,
    @SerialName("insoluble-fiber_100g")
    val insolubleFiber: Double? = null,
    @SerialName("insoluble-fiber_unit")
    val insolubleFiberUnit: String? = null,
    @SerialName("cholesterol_100g")
    val cholesterol: Double? = null,
    @SerialName("cholesterol_unit")
    val cholesterolUnit: String? = null,
    @SerialName("caffeine_100g")
    val caffeine: Double? = null,
    @SerialName("caffeine_unit")
    val caffeineUnit: String? = null,
    // Vitamins
    @SerialName("vitamin-a_100g")
    val vitaminA: Double? = null,
    @SerialName("vitamin-a_unit")
    val vitaminAUnit: String? = null,
    @SerialName("vitamin-b1_100g")
    val vitaminB1: Double? = null,
    @SerialName("vitamin-b1_unit")
    val vitaminB1Unit: String? = null,
    @SerialName("vitamin-b2_100g")
    val vitaminB2: Double? = null,
    @SerialName("vitamin-b2_unit")
    val vitaminB2Unit: String? = null,
    @SerialName("vitamin-pp_100g")
    val vitaminB3: Double? = null,
    @SerialName("vitamin-pp_unit")
    val vitaminB3Unit: String? = null,
    @SerialName("pantothenic-acid_100g")
    val vitaminB5: Double? = null,
    @SerialName("pantothenic-acid_unit")
    val vitaminB5Unit: String? = null,
    @SerialName("vitamin-b6_100g")
    val vitaminB6: Double? = null,
    @SerialName("vitamin-b6_unit")
    val vitaminB6Unit: String? = null,
    @SerialName("biotin_100g")
    val vitaminB7: Double? = null,
    @SerialName("biotin_unit")
    val vitaminB7Unit: String? = null,
    @SerialName("vitamin-b9_100g")
    val vitaminB9: Double? = null,
    @SerialName("vitamin-b9_unit")
    val vitaminB9Unit: String? = null,
    @SerialName("vitamin-b12_100g")
    val vitaminB12: Double? = null,
    @SerialName("vitamin-b12_unit")
    val vitaminB12Unit: String? = null,
    @SerialName("vitamin-c_100g")
    val vitaminC: Double? = null,
    @SerialName("vitamin-c_unit")
    val vitaminCUnit: String? = null,
    @SerialName("vitamin-d_100g")
    val vitaminD: Double? = null,
    @SerialName("vitamin-d_unit")
    val vitaminDUnit: String? = null,
    @SerialName("vitamin-e_100g")
    val vitaminE: Double? = null,
    @SerialName("vitamin-e_unit")
    val vitaminEUnit: String? = null,
    @SerialName("vitamin-k_100g")
    val vitaminK: Double? = null,
    @SerialName("vitamin-k_unit")
    val vitaminKUnit: String? = null,
    // Minerals
    @SerialName("manganese_100g")
    val manganese: Double? = null,
    @SerialName("manganese_unit")
    val manganeseUnit: String? = null,
    @SerialName("magnesium_100g")
    val magnesium: Double? = null,
    @SerialName("magnesium_unit")
    val magnesiumUnit: String? = null,
    @SerialName("potassium_100g")
    val potassium: Double? = null,
    @SerialName("potassium_unit")
    val potassiumUnit: String? = null,
    @SerialName("calcium_100g")
    val calcium: Double? = null,
    @SerialName("calcium_unit")
    val calciumUnit: String? = null,
    @SerialName("copper_100g")
    val copper: Double? = null,
    @SerialName("copper_unit")
    val copperUnit: String? = null,
    @SerialName("zinc_100g")
    val zinc: Double? = null,
    @SerialName("zinc_unit")
    val zincUnit: String? = null,
    @SerialName("sodium_100g")
    val sodium: Double? = null,
    @SerialName("sodium_unit")
    val sodiumUnit: String? = null,
    @SerialName("iron_100g")
    val iron: Double? = null,
    @SerialName("iron_unit")
    val ironUnit: String? = null,
    @SerialName("phosphorus_100g")
    val phosphorus: Double? = null,
    @SerialName("phosphorus_unit")
    val phosphorusUnit: String? = null,
    @SerialName("selenium_100g")
    val selenium: Double? = null,
    @SerialName("selenium_unit")
    val seleniumUnit: String? = null,
    @SerialName("iodine_100g")
    val iodine: Double? = null,
    @SerialName("iodine_unit")
    val iodineUnit: String? = null,
    @SerialName("chromium_100g")
    val chromium: Double? = null,
    @SerialName("chromium_unit")
    val chromiumUnit: String? = null
)
