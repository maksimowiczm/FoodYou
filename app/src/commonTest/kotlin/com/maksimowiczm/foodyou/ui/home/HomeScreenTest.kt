package com.maksimowiczm.foodyou.ui.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.core.ui.LocalHomeSharedTransitionScope
import com.maksimowiczm.foodyou.ext.AnimatedSharedTransitionLayout
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
        onMealCardClick: (epochDay: Int, mealId: Long) -> Unit = { _, _ -> },
        onMealCardAddClick: (epochDay: Int, mealId: Long) -> Unit = { _, _ -> },
        onCaloriesCardClick: (epochDay: Int) -> Unit = {}
    ) {
        AnimatedSharedTransitionLayout {
            CompositionLocalProvider(
                LocalHomeSharedTransitionScope provides sharedTransitionScope
            ) {
                HomeScreen(
                    animatedVisibilityScope = animatedVisibilityScope,
                    order = order,
                    onSettings = onSettings,
                    onAbout = onAbout,
                    onMealCardClick = onMealCardClick,
                    onMealCardAddClick = onMealCardAddClick,
                    onCaloriesCardClick = onCaloriesCardClick,
                    modifier = modifier
                )
            }
        }
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
