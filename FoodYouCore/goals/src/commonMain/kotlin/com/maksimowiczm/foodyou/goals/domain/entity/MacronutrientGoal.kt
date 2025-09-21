package com.maksimowiczm.foodyou.goals.domain.entity

import com.maksimowiczm.foodyou.shared.domain.food.NutrientsHelper
import kotlin.math.roundToInt

sealed interface MacronutrientGoal {
    val energyKcal: Double
    val proteinsGrams: Double
    val fatsGrams: Double
    val carbohydratesGrams: Double

    /** Represents macronutrient goals defined in grams. */
    data class Manual(
        override val energyKcal: Double,
        override val proteinsGrams: Double,
        override val fatsGrams: Double,
        override val carbohydratesGrams: Double,
    ) : MacronutrientGoal

    /** Represents macronutrient goals defined as a distribution in percentages. */
    data class Distribution(
        override val energyKcal: Double,
        val proteinsPercentage: Double,
        val fatsPercentage: Double,
        val carbohydratesPercentage: Double,
    ) : MacronutrientGoal {
        init {
            require(proteinsPercentage >= 0) { "Proteins percentage must be non-negative" }
            require(fatsPercentage >= 0) { "Fats percentage must be non-negative" }
            require(carbohydratesPercentage >= 0) {
                "Carbohydrates percentage must be non-negative"
            }
        }

        override val proteinsGrams: Double =
            NutrientsHelper.proteinsPercentageToGrams(energyKcal.roundToInt(), proteinsPercentage)

        override val fatsGrams: Double =
            NutrientsHelper.fatsPercentageToGrams(energyKcal.roundToInt(), fatsPercentage)

        override val carbohydratesGrams: Double =
            NutrientsHelper.carbohydratesPercentageToGrams(
                energyKcal.roundToInt(),
                carbohydratesPercentage,
            )
    }
}
