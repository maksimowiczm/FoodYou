package com.maksimowiczm.foodyou.ui.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.ext.onNodeWithTag
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HomeScreenTest {

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    private fun HomeScreen(
        modifier: Modifier = Modifier,
        order: List<HomeCard> = HomeCard.entries,
        onSettings: () -> Unit = {},
        onAbout: () -> Unit = {},
        onEditMeasurement: (MeasurementId) -> Unit = {},
        onMealCardLongClick: () -> Unit = {},
        onMealCardAddClick: (epochDay: Int, mealId: Long) -> Unit = { _, _ -> },
        onGoalsCardClick: (epochDay: Int) -> Unit = {},
        onGoalsCardLongClick: () -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.ui.home.HomeScreen(
            order = order,
            onSettings = onSettings,
            onAbout = onAbout,
            onEditMeasurement = onEditMeasurement,
            onMealCardLongClick = onMealCardLongClick,
            onMealCardAddClick = onMealCardAddClick,
            onGoalsCardClick = onGoalsCardClick,
            onGoalsCardLongClick = onGoalsCardLongClick,
            modifier = modifier
        )
    }

    fun verify_layout(order: List<HomeCard>) = runComposeUiTest {
        setContent {
            HomeScreen(
                order = order
            )
        }

        onNodeWithTag(HomeScreenTestTags.CARDS_LIST).assertExists()

        order.forEach {
            val cardTag = HomeScreenTestTags.Card(it).toString()
            onNodeWithTag(HomeScreenTestTags.CARDS_LIST).performScrollToNode(hasTestTag(cardTag))
            onNodeWithTag(cardTag).assertIsDisplayed()
        }

        (HomeCard.entries - order).forEach {
            val cardTag = HomeScreenTestTags.Card(it).toString()
            onNodeWithTag(cardTag).assertDoesNotExist()
        }
    }

    @Test
    fun verify_default_layout() = verify_layout(HomeCard.entries)

    @Test
    fun verify_empty_layout() = verify_layout(emptyList())
}
