package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsNutrients(
    @SerialName("proteins_100g")
    val proteins: Double? = null,
    @SerialName("carbohydrates_100g")
    val carbohydrates: Double? = null,
    @SerialName("fat_100g")
    val fats: Double? = null,
    @SerialName("energy-kcal_100g")
    val energy: Double? = null,
    @SerialName("saturated-fat_100g")
    val saturatedFats: Double? = null,
    @SerialName("sugars_100g")
    val sugars: Double? = null,
    @SerialName("salt_100g")
    val salt: Double? = null,
    @SerialName("fiber_100g")
    val fiber: Double? = null,
    // Vitamins
    @SerialName("vitamin-a_100g")
    val vitaminA: Double? = null,
    @SerialName("vitamin-b1_100g")
    val vitaminB1: Double? = null,
    @SerialName("vitamin-b2_100g")
    val vitaminB2: Double? = null,
    @SerialName("vitamin-b3_100g")
    val vitaminB3: Double? = null,
    @SerialName("vitamin-b5_100g")
    val vitaminB5: Double? = null,
    @SerialName("vitamin-b6_100g")
    val vitaminB6: Double? = null,
    @SerialName("vitamin-b7_100g")
    val vitaminB7: Double? = null,
    @SerialName("vitamin-b9_100g")
    val vitaminB9: Double? = null,
    @SerialName("vitamin-b12_100g")
    val vitaminB12: Double? = null,
    @SerialName("vitamin-c_100g")
    val vitaminC: Double? = null,
    @SerialName("vitamin-d_100g")
    val vitaminD: Double? = null,
    @SerialName("vitamin-e_100g")
    val vitaminE: Double? = null,
    @SerialName("vitamin-k_100g")
    val vitaminK: Double? = null,
    // Minerals
    @SerialName("manganese_100g")
    val manganese: Double? = null,
    @SerialName("magnesium_100g")
    val magnesium: Double? = null,
    @SerialName("potassium_100g")
    val potassium: Double? = null,
    @SerialName("calcium_100g")
    val calcium: Double? = null,
    @SerialName("copper_100g")
    val copper: Double? = null,
    @SerialName("zinc_100g")
    val zinc: Double? = null,
    @SerialName("sodium_100g")
    val sodium: Double? = null,
    @SerialName("iron_100g")
    val iron: Double? = null,
    @SerialName("phosphorus_100g")
    val phosphorus: Double? = null,
    @SerialName("selenium_100g")
    val selenium: Double? = null,
    @SerialName("iodine_100g")
    val iodine: Double? = null
)
