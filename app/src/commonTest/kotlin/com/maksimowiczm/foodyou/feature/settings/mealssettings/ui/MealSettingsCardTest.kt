package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import org.junit.Test

class MealSettingsCardTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun mealSettingsCardAllDayTest() = runComposeUiTest {
        setContent {
            MealSettingsCard(
                state = rememberMealsSettingsCardState(),
                showDeleteDialog = false,
                onDelete = {},
                onConfirm = {},
                formatTime = { it.toString() }
            )
        }

        onNodeWithTag("Test").assertDoesNotExist()
    }
}
