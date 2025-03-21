package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import kotlinx.datetime.LocalDateTime
import org.junit.Test

class AddFoodSearchBarTest {
    /**
     * Since search bar uses dialog it uses 2 different text fields for collapsed and expanded
     * state. This test literally tests if material library is working correctly and can be
     * removed. It exists only because of bugs with material search bar on API < 28 but it's not
     * needed anymore since we don't support API < 28.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun searchBarExpandToggleTest() = runComposeUiTest {
        setContent {
            SearchTopBar(
                state = rememberSearchTopBarState(
                    recentQueries = listOf(
                        ProductQuery(
                            "Apple",
                            LocalDateTime(2025, 2, 13, 12, 50)
                        )
                    )
                ),
                onBarcodeScanner = {},
                onSearch = {},
                onClearSearch = {},
                onBack = {}
            )
        }

        onNodeWithTag("CollapsedSearchBarInput").assertIsDisplayed()
        onNodeWithTag("ExpandedSearchBarInput").assertDoesNotExist()
        onNodeWithTag("SearchResults").assertDoesNotExist()

        onNodeWithTag("CollapsedSearchBarInput").performClick()
        onNodeWithTag("ExpandedSearchBarInput").assertIsDisplayed()
        onNodeWithTag("SearchResults").assertIsDisplayed()

        onNodeWithTag("ExpandedSearchBarInput").performTextInput("Apple")
        onNodeWithTag("ExpandedSearchBarInput").performImeAction()

        onNodeWithTag("ExpandedSearchBarInput").assertDoesNotExist()
        onNodeWithTag("SearchResults").assertDoesNotExist()

        onNodeWithTag("CollapsedSearchBarInput").assertIsDisplayed()
        onNodeWithTag("CollapsedSearchBarInput").assertTextEquals("Apple")

        onNodeWithTag("CollapsedSearchBarInput").assertIsNotFocused()
    }
}
