package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.datetime.LocalTime
import org.junit.Test

class MealSettingsCardTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mealSettingsCardAllDayTest() = runComposeUiTest {
        setContent {
            MealSettingsCard(
                state = rememberMealsSettingsCardState(
                    meal = Meal(
                        id = 0,
                        name = "Test",
                        from = LocalTime(12, 0, 0),
                        to = LocalTime(14, 0, 0)
                    )
                ),
                showDeleteDialog = false,
                onDelete = {},
                onConfirm = {},
                formatTime = { it.toString() }
            )
        }

        onNodeWithTag(MealSettingsCardTestTags.NAME_INPUT)
            .assertIsDisplayed()
            .assertTextEquals("Test")

        onNodeWithTag(MealSettingsCardTestTags.DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(MealSettingsCardTestTags.CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(MealSettingsCardTestTags.LOADING_INDICATOR).assertDoesNotExist()

        onNodeWithTag(MealSettingsCardTestTags.ALL_DAY_SWITCH).assertIsDisplayed()
        onNodeWithTag(MealSettingsCardTestTags.TIME_PICKER).assertIsDisplayed()
    }
}
