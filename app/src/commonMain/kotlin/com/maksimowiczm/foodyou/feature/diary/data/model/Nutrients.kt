package com.maksimowiczm.foodyou.feature.diary.data.model

/**
 * Nutritional values of the product per 100 grams.
 */
data class Nutrients(
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val sugars: NutrientValue,
    val fats: Float,
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

fun Iterable<Nutrients>.sum(): Nutrients = fold(
    initial = Nutrients(
        calories = 0f,
        proteins = 0f,
        carbohydrates = 0f,
        sugars = NutrientValue.Complete(0f),
        fats = 0f,
        saturatedFats = NutrientValue.Complete(0f),
        salt = NutrientValue.Complete(0f),
        sodium = NutrientValue.Complete(0f),
        fiber = NutrientValue.Complete(0f)
    ),
    operation = Nutrients::plus
)
