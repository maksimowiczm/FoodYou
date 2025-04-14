package com.maksimowiczm.foodyou.core.mapper

import com.maksimowiczm.foodyou.core.data.model.Nutrients as NutrientsEntity
import com.maksimowiczm.foodyou.core.model.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients

object NutrientsMapper {
    fun NutrientsEntity.toModel(): Nutrients = Nutrients(
        calories = calories.toNutrientValue(),
        proteins = proteins.toNutrientValue(),
        carbohydrates = carbohydrates.toNutrientValue(),
        sugars = sugars.toNutrientValue(),
        fats = fats.toNutrientValue(),
        saturatedFats = saturatedFats.toNutrientValue(),
        salt = salt.toNutrientValue(),
        sodium = sodium.toNutrientValue(),
        fiber = fiber.toNutrientValue()
    )
}
