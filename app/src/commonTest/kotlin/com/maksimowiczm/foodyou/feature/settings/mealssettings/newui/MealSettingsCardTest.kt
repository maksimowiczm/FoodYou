package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.ALL_DAY_SWITCH
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.CONFIRM_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.DELETE_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.FROM_TIME_PICKER
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.LOADING_INDICATOR
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.NAME_INPUT
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.TIME_PICKER
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.TO_TIME_PICKER
import kotlinx.datetime.LocalTime
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MealSettingsCardTest {
    fun ComposeUiTest.setupCard(
        name: String,
        from: LocalTime,
        to: LocalTime,
        formatTime: (LocalTime) -> String
    ) {
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
    }

    @Test
    fun test_initial_ui_time_frames_not_equal() = runComposeUiTest {
        val name = "Test"
        val from = LocalTime(12, 0, 0)
        val to = LocalTime(14, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setupCard(name, from, to, formatTime)

        onNodeWithTag(NAME_INPUT).assertIsDisplayed().assertTextEquals(name)
        onNodeWithTag(FROM_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(from))
        onNodeWithTag(TO_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(to))

        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(LOADING_INDICATOR).assertDoesNotExist()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed()
        onNodeWithTag(TIME_PICKER).assertIsDisplayed()
    }

    @Test
    fun test_all_day_toggle_initial_time_frames_not_equal() = runComposeUiTest {
        val name = "Test"
        val from = LocalTime(12, 0, 0)
        val to = LocalTime(14, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setupCard(name, from, to, formatTime)

        onNodeWithTag(ALL_DAY_SWITCH).performClick()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed().assertIsOn()
        onNodeWithTag(TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(FROM_TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(TO_TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(CONFIRM_BUTTON).assertIsDisplayed().assertIsEnabled()
        onNodeWithTag(DELETE_BUTTON).assertDoesNotExist()
        onNodeWithTag(LOADING_INDICATOR).assertDoesNotExist()
    }

    @Test
    fun test_initial_ui_time_frames_equal() = runComposeUiTest {
        val name = "Test"
        val time = LocalTime(12, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setupCard(name, time, time, formatTime)

        onNodeWithTag(NAME_INPUT).assertIsDisplayed().assertTextEquals(name)
        onNodeWithTag(FROM_TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(TO_TIME_PICKER).assertDoesNotExist()

        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(LOADING_INDICATOR).assertDoesNotExist()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed().assertIsOn()
        onNodeWithTag(TIME_PICKER).assertDoesNotExist()
    }

    @Test
    fun test_all_day_toggle_initial_time_frames_equal() = runComposeUiTest {
        val name = "Test"
        val time = LocalTime(12, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setupCard(name, time, time, formatTime)

        onNodeWithTag(ALL_DAY_SWITCH).performClick()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed().assertIsOff()
        onNodeWithTag(TIME_PICKER).assertIsDisplayed()
        onNodeWithTag(FROM_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(time))
        onNodeWithTag(TO_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(time))
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(LOADING_INDICATOR).assertDoesNotExist()
    }
}
