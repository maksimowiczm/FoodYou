package com.maksimowiczm.foodyou.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.ext.onNodeWithTag
import com.maksimowiczm.foodyou.ui.home.HomeSettingsScreenTestTags.CARDS_LIST
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HomeSettingsScreenTest {

    @Composable
    fun HomeSettingsScreen(
        modifier: Modifier = Modifier,
        order: List<HomeCard> = HomeCard.entries,
        onBack: () -> Unit = {},
        onMealsSettings: () -> Unit = {},
        onGoalsSettings: () -> Unit = {},
        onReorder: (List<HomeCard>) -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.ui.home.HomeSettingsScreen(
            order = order,
            onBack = onBack,
            onMealsSettings = onMealsSettings,
            onGoalsSettings = onGoalsSettings,
            onReorder = onReorder,
            modifier = modifier
        )
    }

    @Test
    fun all_cards_visible() = runComposeUiTest {
        setContent {
            HomeSettingsScreen(
                order = HomeCard.entries
            )
        }

        HomeCard.entries.forEach {
            val cardTag = HomeSettingsScreenTestTags.Card(it)
            onNodeWithTag(CARDS_LIST).performScrollToNode(hasTestTag(cardTag.toString()))
            onNodeWithTag(cardTag).assertIsDisplayed()
        }
    }
}
