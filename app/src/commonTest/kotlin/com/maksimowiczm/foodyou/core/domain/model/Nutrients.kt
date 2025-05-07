package com.maksimowiczm.foodyou.core.domain.model

fun testNutrients(
    calories: Float = 100f,
    protein: Float = 10f,
    carbohydrates: Float = 15f,
    sugars: Float = 5f,
    fats: Float = 5f,
    saturatedFats: Float = 2f,
    salt: Float = 0.5f,
    sodium: Float = 0.2f,
    fiber: Float = 3f
) = Nutrients(
    calories = NutrientValue.Complete(calories),
    proteins = NutrientValue.Complete(protein),
    carbohydrates = NutrientValue.Complete(carbohydrates),
    sugars = NutrientValue.Complete(sugars),
    fats = NutrientValue.Complete(fats),
    saturatedFats = NutrientValue.Complete(saturatedFats),
    salt = NutrientValue.Complete(salt),
    sodium = NutrientValue.Complete(sodium),
    fiber = NutrientValue.Complete(fiber)
)
