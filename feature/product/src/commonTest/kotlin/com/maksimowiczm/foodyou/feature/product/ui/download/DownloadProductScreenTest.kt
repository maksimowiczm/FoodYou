package com.maksimowiczm.foodyou.feature.product.ui.download

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DownloadProductScreenTest {

    // This tests would require scrolling if the screen is too small but for now it is not needed
    @Composable
    private fun DownloadProductScreen(
        modifier: Modifier = Modifier,
        isMutating: Boolean = false,
        error: DownloadError? = null,
        textFieldState: TextFieldState = rememberTextFieldState(),
        onBack: () -> Unit = {},
        onDownload: () -> Unit = {},
        onPaste: () -> Unit = {},
        onOpenFoodFacts: () -> Unit = {},
        onSuggestDatabase: () -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreen(
            isMutating = isMutating,
            error = error,
            textFieldState = textFieldState,
            onBack = onBack,
            onDownload = onDownload,
            onPaste = onPaste,
            onOpenFoodFacts = onOpenFoodFacts,
            onSuggestDatabase = onSuggestDatabase,
            modifier = modifier
        )
    }

    @Test
    fun idleState() = runComposeUiTest {
        setContent {
            DownloadProductScreen()
        }

        onNodeWithTag(DownloadProductScreenTestTags.PROGRESS_INDICATOR).assertDoesNotExist()
        onNodeWithTag(DownloadProductScreenTestTags.ERROR_CARD).assertDoesNotExist()
        onNodeWithTag(DownloadProductScreenTestTags.TEXT_FIELD).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.DOWNLOAD_FAB).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.PASTE_FAB).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.PASTE_URL_CHIP).assertExists().assertIsEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.OPEN_FOOD_FACTS_CHIP).assertExists()
            .assertIsEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.SUGGEST_DATABASE_CHIP).assertExists()
            .assertIsEnabled()
    }

    @Test
    fun mutatingState() = runComposeUiTest {
        setContent {
            DownloadProductScreen(isMutating = true)
        }

        onNodeWithTag(DownloadProductScreenTestTags.PROGRESS_INDICATOR).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.ERROR_CARD).assertDoesNotExist()
        onNodeWithTag(DownloadProductScreenTestTags.TEXT_FIELD).assertExists().assertIsNotEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.DOWNLOAD_FAB).assertIsNotDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.PASTE_FAB).assertIsNotDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.PASTE_URL_CHIP).assertExists()
            .assertIsNotEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.OPEN_FOOD_FACTS_CHIP).assertExists()
            .assertIsEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.SUGGEST_DATABASE_CHIP).assertExists()
            .assertIsEnabled()
    }

    @Test
    fun errorState() = runComposeUiTest {
        setContent {
            DownloadProductScreen(
                error = DownloadError.Custom("Error message")
            )
        }

        onNodeWithTag(DownloadProductScreenTestTags.PROGRESS_INDICATOR).assertDoesNotExist()
        onNodeWithTag(DownloadProductScreenTestTags.ERROR_CARD).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.TEXT_FIELD).assertExists().assertIsEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.DOWNLOAD_FAB).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.PASTE_FAB).assertIsDisplayed()
        onNodeWithTag(DownloadProductScreenTestTags.PASTE_URL_CHIP).assertExists().assertIsEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.OPEN_FOOD_FACTS_CHIP).assertExists()
            .assertIsEnabled()
        onNodeWithTag(DownloadProductScreenTestTags.SUGGEST_DATABASE_CHIP).assertExists()
            .assertIsEnabled()
    }
}
