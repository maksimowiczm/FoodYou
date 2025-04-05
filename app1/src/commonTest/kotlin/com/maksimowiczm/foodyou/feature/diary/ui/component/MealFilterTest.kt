package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.ext.onNodeWithTag
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import kotlinx.datetime.LocalTime
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MealFilterTest {
    private val breakfast = Meal(
        id = 1,
        name = "Breakfast",
        from = LocalTime(8, 0),
        to = LocalTime(10, 0),
        rank = 0
    )

    private val lunch = Meal(
        id = 2,
        name = "Lunch",
        from = LocalTime(12, 0),
        to = LocalTime(14, 0),
        rank = 1
    )

    private val dinner = Meal(
        id = 3,
        name = "Dinner",
        from = LocalTime(18, 0),
        to = LocalTime(20, 0),
        rank = 2
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
            .assertIsSelected()
        onNodeWithTag(lunch.mealChipIconTestTag()).assertDoesNotExist()
        onNodeWithTag(dinner.mealChipIconTestTag()).assertDoesNotExist()
    }
}

private fun Meal.mealChipTestTag() = MealsFilterTestTags.MealChip(this)

private fun Meal.mealChipIconTestTag() = MealsFilterTestTags.MealChipIcon(this)
