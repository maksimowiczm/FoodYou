package com.maksimowiczm.foodyou.app.infrastructure.room.shared

data class Nutrients(
    val energy: Double?,
    val proteins: Double?,
    val fats: Double?,
    val saturatedFats: Double?,
    val transFats: Double?,
    val monounsaturatedFats: Double?,
    val polyunsaturatedFats: Double?,
    val omega3: Double?,
    val omega6: Double?,
    val carbohydrates: Double?,
    val sugars: Double?,
    val addedSugars: Double?,
    val dietaryFiber: Double?,
    val solubleFiber: Double?,
    val insolubleFiber: Double?,
    val salt: Double?,
    val cholesterolMilli: Double?,
    val caffeineMilli: Double?,
)
