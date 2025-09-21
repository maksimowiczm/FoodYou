package com.maksimowiczm.foodyou.goals.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.NutritionFactsField

/**
 * Represents daily nutritional goals for a user.
 *
 * @property isDistribution Indicates whether the macronutrient goals are set as a distribution in
 *   percentages.
 */
data class DailyGoal(
    val macronutrientGoal: MacronutrientGoal,
    val map: Map<NutritionFactsField, Double>,
) {
    val isDistribution: Boolean
        get() = macronutrientGoal is MacronutrientGoal.Distribution

    /** Retrieves the goal value for the specified nutritional field. */
    operator fun get(field: NutritionFactsField): Double =
        when (field) {
            NutritionFactsField.Energy -> macronutrientGoal.energyKcal
            NutritionFactsField.Proteins -> macronutrientGoal.proteinsGrams
            NutritionFactsField.Fats -> macronutrientGoal.fatsGrams
            NutritionFactsField.Carbohydrates -> macronutrientGoal.carbohydratesGrams
            else -> map[field] ?: error("Field $field not found in DailyGoal")
        }

    companion object {

        private val defaultMacronutrientGoal: MacronutrientGoal =
            MacronutrientGoal.Distribution(
                energyKcal = 2000.0,
                proteinsPercentage = .2,
                fatsPercentage = .3,
                carbohydratesPercentage = .5,
            )

        /**
         * Provides a default set of daily nutritional goals based on general dietary
         * recommendations.
         */
        val defaultGoals: DailyGoal
            get() {
                val map =
                    NutritionFactsField.entries
                        .associateWith {
                            when (it) {
                                NutritionFactsField.Energy -> null
                                NutritionFactsField.Proteins -> null
                                NutritionFactsField.Fats -> null
                                NutritionFactsField.SaturatedFats -> 18.0
                                NutritionFactsField.TransFats -> 0.0
                                NutritionFactsField.MonounsaturatedFats -> 25.0
                                NutritionFactsField.PolyunsaturatedFats -> 18.0
                                NutritionFactsField.Omega3 -> 1.4
                                NutritionFactsField.Omega6 -> 9.0
                                NutritionFactsField.Carbohydrates -> null
                                NutritionFactsField.Sugars -> 50.0
                                NutritionFactsField.AddedSugars -> 25.0
                                NutritionFactsField.DietaryFiber -> 30.0
                                NutritionFactsField.SolubleFiber -> 6.0
                                NutritionFactsField.InsolubleFiber -> 22.0
                                NutritionFactsField.Salt -> 5.0
                                NutritionFactsField.Cholesterol -> 0.3
                                NutritionFactsField.Caffeine -> 0.4

                                NutritionFactsField.VitaminA -> 0.0009
                                NutritionFactsField.VitaminB1 -> 0.0012
                                NutritionFactsField.VitaminB2 -> 0.0012
                                NutritionFactsField.VitaminB3 -> 0.016
                                NutritionFactsField.VitaminB5 -> 0.005
                                NutritionFactsField.VitaminB6 -> 0.0017
                                NutritionFactsField.VitaminB7 -> 0.00003
                                NutritionFactsField.VitaminB9 -> 0.0004
                                NutritionFactsField.VitaminB12 -> 0.0000024
                                NutritionFactsField.VitaminC -> 0.045
                                NutritionFactsField.VitaminD -> 0.00002
                                NutritionFactsField.VitaminE -> 0.015
                                NutritionFactsField.VitaminK -> 0.00012

                                NutritionFactsField.Manganese -> 0.0023
                                NutritionFactsField.Magnesium -> 0.42
                                NutritionFactsField.Potassium -> 4.7
                                NutritionFactsField.Calcium -> 1.2
                                NutritionFactsField.Copper -> 0.0009
                                NutritionFactsField.Zinc -> 0.011
                                NutritionFactsField.Sodium -> 2.3
                                NutritionFactsField.Iron -> 0.008
                                NutritionFactsField.Phosphorus -> 0.7
                                NutritionFactsField.Selenium -> 0.000055
                                NutritionFactsField.Iodine -> 0.00015
                                NutritionFactsField.Chromium -> 0.000035
                            }
                        }
                        .filterValues { it != null }
                        .mapValues { it.value!! }

                return DailyGoal(defaultMacronutrientGoal, map)
            }
    }
}
