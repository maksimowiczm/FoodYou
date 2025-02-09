package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class AddFoodSearchBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBarExpandToggleTest() {
        composeTestRule.setContent {
            AddFoodSearchBar(
                searchBarState = rememberSearchBarState(),
                onSearchSettings = {},
                onSearch = {},
                onClearSearch = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithTag("SearchBarInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchBarInput").performClick()
        composeTestRule.onNodeWithTag("SearchBarInput").assertIsFocused()

        composeTestRule.onNodeWithTag("SearchBarInput").performTextInput("Test")
        composeTestRule.onNodeWithTag("SearchBarInput").performImeAction()

        composeTestRule.onNodeWithTag("SearchBarInput").assertIsNotFocused()
    }
}
