package com.maksimowiczm.foodyou.feature.food.data

data class Nutrients(
    // Macronutrients
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val calories: Float,
    // Fats
    val saturatedFats: Float?,
    val monounsaturatedFats: Float?,
    val polyunsaturatedFats: Float?,
    val omega3: Float?,
    val omega6: Float?,
    // Other
    val sugars: Float?,
    val salt: Float?,
    val fiber: Float?,
    val cholesterolMilli: Float?,
    val caffeineMilli: Float?
)
