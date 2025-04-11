package com.maksimowiczm.foodyou.core.model

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

    operator fun times(multiplier: Float): Nutrients = Nutrients(
        calories = this.calories * multiplier,
        proteins = this.proteins * multiplier,
        carbohydrates = this.carbohydrates * multiplier,
        sugars = this.sugars * multiplier,
        fats = this.fats * multiplier,
        saturatedFats = this.saturatedFats * multiplier,
        salt = this.salt * multiplier,
        sodium = this.sodium * multiplier,
        fiber = this.fiber * multiplier
    )

    operator fun div(divisor: Float): Nutrients = Nutrients(
        calories = this.calories / divisor,
        proteins = this.proteins / divisor,
        carbohydrates = this.carbohydrates / divisor,
        sugars = this.sugars / divisor,
        fats = this.fats / divisor,
        saturatedFats = this.saturatedFats / divisor,
        salt = this.salt / divisor,
        sodium = this.sodium / divisor,
        fiber = this.fiber / divisor
    )
}

fun Iterable<Nutrients>.sum(): Nutrients = fold(
    Nutrients(
        calories = NutrientValue.Complete(0f),
        proteins = NutrientValue.Complete(0f),
        carbohydrates = NutrientValue.Complete(0f),
        sugars = NutrientValue.Complete(0f),
        fats = NutrientValue.Complete(0f),
        saturatedFats = NutrientValue.Complete(0f),
        salt = NutrientValue.Complete(0f),
        sodium = NutrientValue.Complete(0f),
        fiber = NutrientValue.Complete(0f)
    )
) { acc, nutrients -> acc + nutrients }
