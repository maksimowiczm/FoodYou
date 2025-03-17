package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.CreateMealSettingsCardTestTags.CREATE_BUTTON
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class CreateMealSettingsCardTest {
    @Test
    fun test_initial() = runComposeUiTest {
        setContent {
            CreateMealSettingsCard()
        }

        onNodeWithTag(CREATE_BUTTON).assertIsDisplayed()
    }
}
