package com.maksimowiczm.foodyou.common.domain.food

import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import kotlin.test.Test
import kotlin.test.assertEquals

class NutritionCalculatorTest {

    @Test
    fun calculate_calories_from_protein() {
        assertEquals(40.0, NutritionCalculator.calculateProteinCalories(10.0))
        assertEquals(40.kilocalories, NutritionCalculator.calculateProteinCalories(10.grams))
    }

    @Test
    fun calculate_calories_from_carbohydrates() {
        assertEquals(40.0, NutritionCalculator.calculateCarbohydrateCalories(10.0))
        assertEquals(40.kilocalories, NutritionCalculator.calculateCarbohydrateCalories(10.grams))
    }

    @Test
    fun calculate_calories_from_fats() {
        assertEquals(90.0, NutritionCalculator.calculateFatCalories(10.0))
        assertEquals(90.kilocalories, NutritionCalculator.calculateFatCalories(10.grams))
    }

    @Test
    fun calculate_total_calories() {
        assertEquals(
            170.0,
            NutritionCalculator.calculateTotalCalories(
                proteinGrams = 10.0,
                carbohydrateGrams = 10.0,
                fatGrams = 10.0,
            ),
        )
        assertEquals(
            170.kilocalories,
            NutritionCalculator.calculateTotalCalories(
                protein = 10.grams,
                carbohydrates = 10.grams,
                fats = 10.grams,
            ),
        )
    }
}
