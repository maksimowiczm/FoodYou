package com.maksimowiczm.foodyou.feature.food.data.database.food

data class Nutrients(
    val energy: Float?,
    val proteins: Float?,
    // Fats
    val fats: Float?,
    val saturatedFats: Float?,
    val transFats: Float?,
    val monounsaturatedFats: Float?,
    val polyunsaturatedFats: Float?,
    val omega3: Float?,
    val omega6: Float?,
    // Carbs
    val carbohydrates: Float,
    val sugars: Float?,
    val addedSugars: Float?,
    val dietaryFiber: Float?,
    val solubleFiber: Float?,
    val insolubleFiber: Float?,
    // Other
    val salt: Float?,
    val cholesterolMilli: Float?,
    val caffeineMilli: Float?
)
