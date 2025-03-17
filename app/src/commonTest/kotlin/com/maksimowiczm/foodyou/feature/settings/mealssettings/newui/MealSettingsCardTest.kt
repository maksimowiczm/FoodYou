package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.ALL_DAY_SWITCH
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.ALL_DAY_SWITCH_CONTAINER
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.CONFIRM_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.DELETE_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.FROM_TIME_PICKER
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.NAME_INPUT
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.TIME_PICKER
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.TO_TIME_PICKER
import kotlinx.datetime.LocalTime
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MealSettingsCardTest {
    @Test
    fun testMealSettingsCardInitialState() = runComposeUiTest {
        val name = "Test"
        val from = LocalTime(12, 0, 0)
        val to = LocalTime(14, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setContent {
            MealSettingsCard(
                state = rememberMealSettingsCardState(
                    meal = Meal(
                        id = 0,
                        name = name,
                        from = from,
                        to = to
                    ),
                    isLoading = false
                ),
                onUpdate = {},
                onDelete = {},
                formatTime = formatTime,
                showDeleteDialog = false
            )
        }

        onNodeWithTag(NAME_INPUT).assertIsDisplayed().assertTextEquals(name)
        onNodeWithTag(FROM_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(from))
        onNodeWithTag(TO_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(to))

        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(MealSettingsCardTestTags.LOADING_INDICATOR).assertDoesNotExist()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed()
        onNodeWithTag(TIME_PICKER).assertIsDisplayed()
    }

    @Test
    fun testAllDaySwitchToggle() = runComposeUiTest {
        val name = "Test"
        val from = LocalTime(12, 0, 0)
        val to = LocalTime(14, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setContent {
            MealSettingsCard(
                state = rememberMealSettingsCardState(
                    meal = Meal(
                        id = 0,
                        name = name,
                        from = from,
                        to = to
                    ),
                    isLoading = false
                ),
                onUpdate = {},
                onDelete = {},
                formatTime = formatTime,
                showDeleteDialog = false
            )
        }

        // Toggle ON
        onNodeWithTag(ALL_DAY_SWITCH_CONTAINER).performClick()
        onNodeWithTag(ALL_DAY_SWITCH).assertIsOn()
        onNodeWithTag(TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(DELETE_BUTTON).assertDoesNotExist()
        onNodeWithTag(CONFIRM_BUTTON).assertIsDisplayed()

        // Toggle OFF
        onNodeWithTag(ALL_DAY_SWITCH).performClick()
        onNodeWithTag(ALL_DAY_SWITCH).assertIsOff()
        onNodeWithTag(TIME_PICKER).assertIsDisplayed()
        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()

        // Assert that same state is restored
        onNodeWithTag(NAME_INPUT).assertIsDisplayed().assertTextEquals(name)
        onNodeWithTag(FROM_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(from))
        onNodeWithTag(TO_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(to))
    }
}
