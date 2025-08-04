package com.maksimowiczm.foodyou.feature.food.domain

import kotlinx.serialization.Serializable

/**
 * Represents the nutritional facts of a 100 g of a product.
 */
@Serializable
internal data class RemoteNutritionFacts(
    // Macronutrients
    val proteins: Float?,
    val carbohydrates: Float?,
    val fats: Float?,
    val energy: Float?,
    // Fats
    val saturatedFats: Float?,
    val transFats: Float?,
    val monounsaturatedFats: Float?,
    val polyunsaturatedFats: Float?,
    val omega3: Float?,
    val omega6: Float?,
    // Other
    val sugars: Float?,
    val addedSugars: Float?,
    val salt: Float?,
    val dietaryFiber: Float?,
    val solubleFiber: Float?,
    val insolubleFiber: Float?,
    val cholesterolMilli: Float?,
    val caffeineMilli: Float?,
    // Vitamins
    val vitaminAMicro: Float?,
    val vitaminB1Milli: Float?,
    val vitaminB2Milli: Float?,
    val vitaminB3Milli: Float?,
    val vitaminB5Milli: Float?,
    val vitaminB6Milli: Float?,
    val vitaminB7Micro: Float?,
    val vitaminB9Micro: Float?,
    val vitaminB12Micro: Float?,
    val vitaminCMilli: Float?,
    val vitaminDMicro: Float?,
    val vitaminEMilli: Float?,
    val vitaminKMicro: Float?,
    // Minerals
    val manganeseMilli: Float?,
    val magnesiumMilli: Float?,
    val potassiumMilli: Float?,
    val calciumMilli: Float?,
    val copperMilli: Float?,
    val zincMilli: Float?,
    val sodiumMilli: Float?,
    val ironMilli: Float?,
    val phosphorusMilli: Float?,
    val seleniumMicro: Float?,
    val iodineMicro: Float?,
    val chromiumMicro: Float?
)
