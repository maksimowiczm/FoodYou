package com.maksimowiczm.foodyou.feature.product.domain

/**
 * Represents the nutritional facts of a 100 g of a product.
 */
data class RemoteNutritionFacts(
    // Macronutrients
    val proteins: Double? = null,
    val carbohydrates: Double? = null,
    val fats: Double? = null,
    val calories: Double? = null,
    // Fats
    val saturatedFats: Double? = null,
    val monounsaturatedFats: Double? = null,
    val polyunsaturatedFats: Double? = null,
    val omega3: Double? = null,
    val omega6: Double? = null,
    // Other
    val sugars: Double? = null,
    val salt: Double? = null,
    val fiber: Double? = null,
    val cholesterolMilli: Double? = null,
    val caffeineMilli: Double? = null,
    // Vitamins
    val vitaminAMicro: Double? = null,
    val vitaminB1Milli: Double? = null,
    val vitaminB2Milli: Double? = null,
    val vitaminB3Milli: Double? = null,
    val vitaminB5Milli: Double? = null,
    val vitaminB6Milli: Double? = null,
    val vitaminB7Micro: Double? = null,
    val vitaminB9Micro: Double? = null,
    val vitaminB12Micro: Double? = null,
    val vitaminCMilli: Double? = null,
    val vitaminDMicro: Double? = null,
    val vitaminEMilli: Double? = null,
    val vitaminKMicro: Double? = null,
    // Minerals
    val manganeseMilli: Double? = null,
    val magnesiumMilli: Double? = null,
    val potassiumMilli: Double? = null,
    val calciumMilli: Double? = null,
    val copperMilli: Double? = null,
    val zincMilli: Double? = null,
    val sodiumMilli: Double? = null,
    val ironMilli: Double? = null,
    val phosphorusMilli: Double? = null,
    val seleniumMicro: Double? = null,
    val iodineMicro: Double? = null
)
