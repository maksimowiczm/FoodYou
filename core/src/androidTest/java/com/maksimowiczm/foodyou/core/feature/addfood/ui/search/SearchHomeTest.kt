package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import android.graphics.Bitmap
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductQuery
import com.maksimowiczm.foodyou.core.feature.addfood.ui.rememberAddFoodState
import com.maksimowiczm.foodyou.core.ui.preview.SharedTransitionPreview
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class SearchHomeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Test
    fun searchBarExpandToggleTest() {
        val recentQueries = listOf(
            ProductQuery(
                query = "Test 1",
                date = LocalDateTime.now()
            )
        )

        composeTestRule.setContent {
            SharedTransitionPreview { _, animatedVisibilityScope ->
                SearchHome(
                    animatedVisibilityScope = animatedVisibilityScope,
                    addFoodState = rememberAddFoodState(
                        searchBarState = rememberSearchBarState(
                            initialRecentQueries = recentQueries
                        )
                    ),
                    onSearchSettings = {},
                    onSearch = {},
                    onClearSearch = {},
                    onRetry = {},
                    onBack = {},
                    onProductClick = {},
                    onCreateProduct = {},
                    onBarcodeScanner = {}
                )
            }
        }

        composeTestRule.waitForIdle()
        val initialScreenshot = composeTestRule.onRoot().captureToImage().asAndroidBitmap()

        composeTestRule.onNodeWithTag("SearchBarInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Content").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SearchViewContent").assertIsNotDisplayed()

        composeTestRule.onNodeWithTag("SearchBarInput").performClick()
        composeTestRule.onNodeWithTag("SearchViewContent").assertIsDisplayed()

        composeTestRule.waitForIdle()
        val expandedScreenshot = composeTestRule.onRoot().captureToImage().asAndroidBitmap()

        composeTestRule.onNodeWithTag("SearchBarInput").performTextInput("Test")
        composeTestRule.onNodeWithTag("SearchBarInput").performImeAction()
        composeTestRule.onNodeWithTag("SearchBarInput").assertTextEquals("Test")

        composeTestRule.waitForIdle()
        val finalScreenshot = composeTestRule.onRoot().captureToImage().asAndroidBitmap()

        // Hack
        // Since the search bar now contains text, we can't compare the screenshots directly. We
        // know that search view has other color than the initial screen, so we can compare the
        // dominant color of the initial and final screenshots.
        val initialDominantColor = initialScreenshot.getDominantColor()
        val expandedDominantColor = expandedScreenshot.getDominantColor()
        val finalDominantColor = finalScreenshot.getDominantColor()
        assert(initialDominantColor == finalDominantColor)
        assert(initialDominantColor != expandedDominantColor)
    }
}

private fun Bitmap.getDominantColor(): Int {
    val colorCount = mutableMapOf<Int, Int>()

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixelColor = getPixel(x, y)
            colorCount[pixelColor] = colorCount.getOrDefault(pixelColor, 0) + 1
        }
    }

    return colorCount.maxByOrNull { it.value }?.key ?: 0xFFFFFF
}
