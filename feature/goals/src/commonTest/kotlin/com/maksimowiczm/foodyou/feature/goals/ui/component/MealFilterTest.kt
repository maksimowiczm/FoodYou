package com.maksimowiczm.foodyou.feature.goals.ui.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.core.model.Meal
import com.maksimowiczm.foodyou.core.model.testing.testMeal
import com.maksimowiczm.foodyou.feature.goals.onNodeWithTag
import org.junit.Test

private fun Meal.mealChipTestTag() = MealsFilterTestTags.MealChip(this)

private fun Meal.mealChipIconTestTag() = MealsFilterTestTags.MealChipIcon(this)

@OptIn(ExperimentalTestApi::class)
class MealFilterTest {
    private val breakfast = testMeal(
        id = 1,
        name = "Breakfast"
    )

    private val lunch = testMeal(
        id = 2,
        name = "Lunch"
    )

    private val dinner = testMeal(
        id = 3,
        name = "Dinner"
    )

    private val meals = setOf(breakfast, lunch, dinner)

    @Test
    fun test_initial() = runComposeUiTest {
        setContent {
            MealsFilter(
                state = rememberMealsFilterState(meals)
            )
        }

        onNodeWithTag(breakfast.mealChipTestTag()).assertIsDisplayed()
        onNodeWithTag(breakfast.mealChipIconTestTag()).assertDoesNotExist()

        onNodeWithTag(lunch.mealChipTestTag()).assertIsDisplayed()
        onNodeWithTag(lunch.mealChipIconTestTag()).assertDoesNotExist()

        onNodeWithTag(dinner.mealChipTestTag()).assertIsDisplayed()
        onNodeWithTag(dinner.mealChipIconTestTag()).assertDoesNotExist()
    }

    @Test
    fun test_click() = runComposeUiTest {
        setContent {
            MealsFilter(
                state = rememberMealsFilterState(meals)
            )
        }

        onNodeWithTag(breakfast.mealChipTestTag())
            .assertIsDisplayed()
            .performClick()
            .assertIsNotSelected()
        onNodeWithTag(lunch.mealChipIconTestTag()).assertDoesNotExist()
        onNodeWithTag(dinner.mealChipIconTestTag()).assertDoesNotExist()
    }
}
