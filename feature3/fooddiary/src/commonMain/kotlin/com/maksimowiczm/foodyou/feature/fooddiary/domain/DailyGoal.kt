package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.NutritionFactsField

data class DailyGoal(private val map: Map<NutritionFactsField, Double>) {
    operator fun get(field: NutritionFactsField) =
        map[field] ?: error("Field $field not found in DailyGoal")

    /**
     * Fills missing fields in the DailyGoal with default values.
     */
    fun fillMissingFields(): DailyGoal {
        val defaultGoals = defaultGoals

        val filledMap = NutritionFactsField.entries.associateWith { field ->
            map[field] ?: defaultGoals[field]
        }

        return DailyGoal(filledMap)
    }

    companion object {
        val defaultGoals = NutritionFactsField.entries.associateWith {
            when (it) {
                NutritionFactsField.Energy -> 2000.0 // kcal
                NutritionFactsField.Proteins -> 50.0 // g
                NutritionFactsField.Fats -> 70.0 // g
                NutritionFactsField.SaturatedFats -> 20.0 // g
                NutritionFactsField.TransFats -> 0.0 // g
                NutritionFactsField.MonounsaturatedFats -> 20.0 // g
                NutritionFactsField.PolyunsaturatedFats -> 15.0 // g
                NutritionFactsField.Omega3 -> 1.6 // g
                NutritionFactsField.Omega6 -> 17.0 // g
                NutritionFactsField.Carbohydrates -> 275.0 // g
                NutritionFactsField.Sugars -> 50.0 // g
                NutritionFactsField.AddedSugars -> 25.0 // g
                NutritionFactsField.DietaryFiber -> 28.0 // g
                NutritionFactsField.SolubleFiber -> 6.0 // g
                NutritionFactsField.InsolubleFiber -> 22.0 // g
                NutritionFactsField.Salt -> 5.0 // g
                NutritionFactsField.Cholesterol -> 0.3 // g (300 mg)
                NutritionFactsField.Caffeine -> 0.4 // g (400 mg)

                NutritionFactsField.VitaminA -> 0.0009 // g (900 µg)
                NutritionFactsField.VitaminB1 -> 0.0012 // g (1.2 mg)
                NutritionFactsField.VitaminB2 -> 0.0013 // g (1.3 mg)
                NutritionFactsField.VitaminB3 -> 0.016 // g (16 mg)
                NutritionFactsField.VitaminB5 -> 0.005 // g (5 mg)
                NutritionFactsField.VitaminB6 -> 0.0013 // g (1.3 mg)
                NutritionFactsField.VitaminB7 -> 0.00003 // g (30 µg)
                NutritionFactsField.VitaminB9 -> 0.0004 // g (400 µg)
                NutritionFactsField.VitaminB12 -> 0.0000024 // g (2.4 µg)
                NutritionFactsField.VitaminC -> 0.09 // g (90 mg)
                NutritionFactsField.VitaminD -> 0.00002 // g (20 µg)
                NutritionFactsField.VitaminE -> 0.015 // g (15 mg)
                NutritionFactsField.VitaminK -> 0.00012 // g (120 µg)

                NutritionFactsField.Manganese -> 0.0023 // g (2.3 mg)
                NutritionFactsField.Magnesium -> 0.4 // g (400 mg)
                NutritionFactsField.Potassium -> 4.7 // g (4700 mg)
                NutritionFactsField.Calcium -> 1.0 // g (1000 mg)
                NutritionFactsField.Copper -> 0.0009 // g (0.9 mg)
                NutritionFactsField.Zinc -> 0.011 // g (11 mg)
                NutritionFactsField.Sodium -> 2.0 // g (2000 mg)
                NutritionFactsField.Iron -> 0.008 // g (8 mg)
                NutritionFactsField.Phosphorus -> 0.7 // g (700 mg)
                NutritionFactsField.Selenium -> 0.000055 // g (55 µg)
                NutritionFactsField.Iodine -> 0.00015 // g (150 µg)
                NutritionFactsField.Chromium -> 0.000035 // g (35 µg)
            }
        }.let(::DailyGoal)
    }
}
