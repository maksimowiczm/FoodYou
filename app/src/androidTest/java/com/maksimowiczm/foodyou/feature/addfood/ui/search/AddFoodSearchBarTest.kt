package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.maksimowiczm.foodyou.data.model.ProductQuery
import com.maksimowiczm.foodyou.ui.feature.addfood.search.SearchTopBar
import com.maksimowiczm.foodyou.ui.feature.addfood.search.rememberSearchTopBarState
import kotlinx.datetime.LocalDateTime
import org.junit.Rule
import org.junit.Test

class AddFoodSearchBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Since search bar uses dialog it uses 2 different text fields for collapsed and expanded
     * state. This test literally tests if material library is working correctly and can be
     * removed. It exists only because of bugs with material search bar on API < 28 but it's not
     * needed anymore since we don't support API < 28.
     */
    @Test
    fun searchBarExpandToggleTest() {
        composeTestRule.setContent {
            SearchTopBar(
                state = rememberSearchTopBarState(
                    recentQueries = listOf(
                        ProductQuery(
                            "Apple",
                            LocalDateTime(2025, 2, 13, 12, 50)
                        )
                    )
                ),
                onSearchSettings = {},
                onSearch = {},
                onClearSearch = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithTag("CollapsedSearchBarInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ExpandedSearchBarInput").assertDoesNotExist()
        composeTestRule.onNodeWithTag("SearchResults").assertDoesNotExist()

        composeTestRule.onNodeWithTag("CollapsedSearchBarInput").performClick()
        composeTestRule.onNodeWithTag("ExpandedSearchBarInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchResults").assertIsDisplayed()

        composeTestRule.onNodeWithTag("ExpandedSearchBarInput").performTextInput("Apple")
        composeTestRule.onNodeWithTag("ExpandedSearchBarInput").performImeAction()

        composeTestRule.onNodeWithTag("ExpandedSearchBarInput").assertDoesNotExist()
        composeTestRule.onNodeWithTag("SearchResults").assertDoesNotExist()

        composeTestRule.onNodeWithTag("CollapsedSearchBarInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CollapsedSearchBarInput").assertTextEquals("Apple")

        composeTestRule.onNodeWithTag("CollapsedSearchBarInput").assertIsNotFocused()
    }
}
