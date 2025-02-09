package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar.AddFoodSearchBar
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.searchbar.rememberSearchBarState
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme
import org.junit.Rule
import org.junit.Test

class AddFoodSearchBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBarExpandToggleTest() {
        composeTestRule.setContent {
            FoodYouTheme {
                AddFoodSearchBar(
                    searchBarState = rememberSearchBarState(),
                    onSearchSettings = {},
                    onSearch = {},
                    onClearSearch = {},
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("SearchBarInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchBarInput").performClick()
        composeTestRule.onNodeWithTag("SearchBarInput").assertIsFocused()

        composeTestRule.onNodeWithTag("SearchBarInput").performTextInput("Test")
        composeTestRule.onNodeWithTag("SearchBarInput").performImeAction()

        // Works only on android API >= 28
        if (Build.VERSION.SDK_INT >= 28) {
            composeTestRule.onNodeWithTag("SearchBarInput").assertIsNotFocused()
        }
    }
}
