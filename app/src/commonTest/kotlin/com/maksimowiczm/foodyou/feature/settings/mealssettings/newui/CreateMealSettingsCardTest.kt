package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.CreateMealSettingsCardTestTags.CREATE_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.CreateMealSettingsCardTestTags.CREATE_MEAL_SETTINGS_CARD
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.CONFIRM_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.DELETE_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.MealSettingsCardTestTags.NAME_INPUT
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class CreateMealSettingsCardTest {
    @Test
    fun test_create_button() = runComposeUiTest {
        setContent {
            CreateMealSettingsCard(
                isCreating = false,
                onCreatingChange = {}
            )
        }

        onNodeWithTag(CREATE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun test_create_button_click() = runComposeUiTest {
        setContent {
            var isCreating by remember { mutableStateOf(false) }

            CreateMealSettingsCard(
                isCreating = isCreating,
                onCreatingChange = { isCreating = it }
            )
        }

        onNodeWithTag(CREATE_BUTTON).assertIsDisplayed().performClick()

        onNodeWithTag(CREATE_BUTTON).assertDoesNotExist()
        onNodeWithTag(CREATE_MEAL_SETTINGS_CARD).assertIsDisplayed()
    }

    @Test
    fun test_delete_button_click() = runComposeUiTest {
        setContent {
            var isCreating by remember { mutableStateOf(true) }

            CreateMealSettingsCard(
                isCreating = isCreating,
                onCreatingChange = { isCreating = it }
            )
        }

        onNodeWithTag(CREATE_MEAL_SETTINGS_CARD).assertIsDisplayed()
        onNodeWithTag(CREATE_BUTTON).assertDoesNotExist()

        onNodeWithTag(DELETE_BUTTON).assertIsDisplayed().performClick()

        onNodeWithTag(CREATE_MEAL_SETTINGS_CARD).assertDoesNotExist()
        onNodeWithTag(CREATE_BUTTON).assertIsDisplayed()
    }

    @Test
    fun test_dirty_state() = runComposeUiTest {
        setContent {
            var isCreating by remember { mutableStateOf(true) }

            CreateMealSettingsCard(
                isCreating = isCreating,
                onCreatingChange = { isCreating = it }
            )
        }

        onNodeWithTag(CREATE_MEAL_SETTINGS_CARD).assertIsDisplayed()
        onNodeWithTag(CREATE_BUTTON).assertDoesNotExist()

        onNodeWithTag(NAME_INPUT).assertIsDisplayed().performTextInput("Test")
        onNodeWithTag(DELETE_BUTTON).assertDoesNotExist()
        onNodeWithTag(CONFIRM_BUTTON).assertIsDisplayed().performClick()

        onNodeWithTag(CREATE_MEAL_SETTINGS_CARD).assertDoesNotExist()
        onNodeWithTag(CREATE_BUTTON).assertIsDisplayed()
    }
}
