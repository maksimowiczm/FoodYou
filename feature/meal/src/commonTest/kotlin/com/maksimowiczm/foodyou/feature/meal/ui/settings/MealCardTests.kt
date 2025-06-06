package com.maksimowiczm.foodyou.feature.meal.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.Meal
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.ALL_DAY_SWITCH
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.CONFIRM_BUTTON
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.DELETE_BUTTON
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.FROM_TIME_PICKER
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.NAME_INPUT
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.TIME_PICKER
import com.maksimowiczm.foodyou.feature.meal.ui.settings.MealCardTestTags.TO_TIME_PICKER
import kotlinx.datetime.LocalTime
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MealCardTests {
    fun ComposeUiTest.setupCard(
        name: String,
        from: LocalTime,
        to: LocalTime,
        action: @Composable (() -> Unit)? = null
    ) {
        val meal = Meal(
            id = 0,
            name = name,
            from = from,
            to = to,
            rank = 0
        )

        setContent {
            MealCard(
                state = MealCardStateWithMeal(
                    meal = meal,
                    nameInput = rememberTextFieldState(meal.name),
                    fromTimeInput = LocalTimeInput(from),
                    toTimeInput = LocalTimeInput(to),
                    isAllDay = mutableStateOf(meal.isAllDay)
                ),
                onSave = {},
                shouldShowDeleteDialog = true,
                onDelete = {},
                action = action
            )
        }
    }

    @Test
    fun test_initial_ui_time_frames_not_equal() = runComposeUiTest {
        val name = "Test"
        val from = LocalTime(12, 0, 0)
        val to = LocalTime(14, 0, 0)

        setupCard(name, from, to)

        onNodeWithTag(NAME_INPUT).assertIsDisplayed().assertTextEquals(name)
        onNodeWithTag(FROM_TIME_PICKER).assertIsDisplayed()
        onNodeWithTag(TO_TIME_PICKER).assertIsDisplayed()

        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed()
        onNodeWithTag(TIME_PICKER).assertIsDisplayed()
    }

    @Test
    fun test_all_day_toggle_initial_time_frames_not_equal() = runComposeUiTest {
        val name = "Test"
        val from = LocalTime(12, 0, 0)
        val to = LocalTime(14, 0, 0)

        setupCard(name, from, to)

        onNodeWithTag(ALL_DAY_SWITCH).performClick()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed().assertIsOn()
        onNodeWithTag(TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(FROM_TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(TO_TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(CONFIRM_BUTTON).assertIsDisplayed().assertIsEnabled()
        onNodeWithTag(DELETE_BUTTON).assertDoesNotExist()
    }

    @Test
    fun test_initial_ui_time_frames_equal() = runComposeUiTest {
        val name = "Test"
        val time = LocalTime(12, 0, 0)

        setupCard(name, time, time)

        onNodeWithTag(NAME_INPUT).assertIsDisplayed().assertTextEquals(name)
        onNodeWithTag(FROM_TIME_PICKER).assertDoesNotExist()
        onNodeWithTag(TO_TIME_PICKER).assertDoesNotExist()

        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed().assertIsOn()
        onNodeWithTag(TIME_PICKER).assertDoesNotExist()
    }

    @Test
    fun test_all_day_toggle_initial_time_frames_equal() = runComposeUiTest {
        val name = "Test"
        val time = LocalTime(12, 0, 0)
        val formatTime: (LocalTime) -> String = { it.toString() }

        setupCard(name, time, time)

        onNodeWithTag(ALL_DAY_SWITCH).performClick()

        onNodeWithTag(ALL_DAY_SWITCH).assertIsDisplayed().assertIsOff()
        onNodeWithTag(TIME_PICKER).assertIsDisplayed()
        onNodeWithTag(FROM_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(time))
        onNodeWithTag(TO_TIME_PICKER).assertIsDisplayed().assertTextEquals(formatTime(time))
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun test_action_button() = runComposeUiTest {
        val name = "Test"
        val time = LocalTime(12, 0, 0)

        setupCard(name, time, time) {
            Box(modifier = Modifier.size(50.dp).testTag("TEST"))
        }

        onNodeWithTag("TEST").assertIsDisplayed()
        onNodeWithTag(CONFIRM_BUTTON).assertDoesNotExist()
        onNodeWithTag(DELETE_BUTTON).assertDoesNotExist()
    }
}
