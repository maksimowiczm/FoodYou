package com.maksimowiczm.foodyou.ui.settings.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HomeSettingsScreenTest {

    @Composable
    fun HomeSettingsScreen(
        modifier: Modifier = Modifier,
        order: List<HomeCard> = HomeCard.entries,
        onBack: () -> Unit = {},
        onMealsSettings: () -> Unit = {},
        onReorder: (List<HomeCard>) -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.ui.settings.home.HomeSettingsScreen(
            order = order,
            onBack = onBack,
            onMealsSettings = onMealsSettings,
            onReorder = onReorder,
            modifier = modifier
        )
    }

    @Test
    fun test_initial() = runComposeUiTest {
        setContent {
            HomeSettingsScreen()
        }
    }
}
