package com.maksimowiczm.foodyou.feature.diary.core.data.food

data class Nutrients(
    val calories: NutrientValue.Complete,
    val proteins: NutrientValue.Complete,
    val carbohydrates: NutrientValue.Complete,
    val sugars: NutrientValue,
    val fats: NutrientValue.Complete,
    val saturatedFats: NutrientValue,
    val salt: NutrientValue,
    val sodium: NutrientValue,
    val fiber: NutrientValue
) {
    val isComplete: Boolean
        get() = listOf(
            sugars,
            saturatedFats,
            salt,
            sodium,
            fiber
        ).all { it is NutrientValue.Complete }

    operator fun plus(other: Nutrients): Nutrients = Nutrients(
        calories = this.calories + other.calories,
        proteins = this.proteins + other.proteins,
        carbohydrates = this.carbohydrates + other.carbohydrates,
        sugars = this.sugars + other.sugars,
        fats = this.fats + other.fats,
        saturatedFats = this.saturatedFats + other.saturatedFats,
        salt = this.salt + other.salt,
        sodium = this.sodium + other.sodium,
        fiber = this.fiber + other.fiber
    )
}
