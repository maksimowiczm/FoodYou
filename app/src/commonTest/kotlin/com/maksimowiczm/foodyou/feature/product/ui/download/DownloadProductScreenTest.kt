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
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.DOWNLOAD_FAB
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.ERROR_CARD
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.OPEN_FOOD_FACTS_CHIP
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.PASTE_FAB
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.PASTE_URL_CHIP
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.PROGRESS_INDICATOR
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.TEXT_FIELD
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
        onOpenFoodFacts: () -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreen(
            isMutating = isMutating,
            error = error,
            textFieldState = textFieldState,
            onBack = onBack,
            onDownload = onDownload,
            onPaste = onPaste,
            onOpenFoodFacts = onOpenFoodFacts,
            modifier = modifier
        )
    }

    @Test
    fun idleState() = runComposeUiTest {
        setContent {
            DownloadProductScreen()
        }

        onNodeWithTag(PROGRESS_INDICATOR).assertDoesNotExist()
        onNodeWithTag(ERROR_CARD).assertDoesNotExist()
        onNodeWithTag(TEXT_FIELD).assertIsDisplayed()
        onNodeWithTag(DOWNLOAD_FAB).assertIsDisplayed()
        onNodeWithTag(PASTE_FAB).assertIsDisplayed()
        onNodeWithTag(PASTE_URL_CHIP).assertExists().assertIsEnabled()
        onNodeWithTag(OPEN_FOOD_FACTS_CHIP).assertExists().assertIsEnabled()
    }

    @Test
    fun mutatingState() = runComposeUiTest {
        setContent {
            DownloadProductScreen(isMutating = true)
        }

        onNodeWithTag(PROGRESS_INDICATOR).assertIsDisplayed()
        onNodeWithTag(ERROR_CARD).assertDoesNotExist()
        onNodeWithTag(TEXT_FIELD).assertExists().assertIsNotEnabled()
        onNodeWithTag(DOWNLOAD_FAB).assertIsNotDisplayed()
        onNodeWithTag(PASTE_FAB).assertIsNotDisplayed()
        onNodeWithTag(PASTE_URL_CHIP).assertExists().assertIsNotEnabled()
        onNodeWithTag(OPEN_FOOD_FACTS_CHIP).assertExists().assertIsEnabled()
    }

    @Test
    fun errorState() = runComposeUiTest {
        setContent {
            DownloadProductScreen(
                error = DownloadError.Custom("Error message")
            )
        }

        onNodeWithTag(PROGRESS_INDICATOR).assertDoesNotExist()
        onNodeWithTag(ERROR_CARD).assertIsDisplayed()
        onNodeWithTag(TEXT_FIELD).assertExists().assertIsEnabled()
        onNodeWithTag(DOWNLOAD_FAB).assertIsDisplayed()
        onNodeWithTag(PASTE_FAB).assertIsDisplayed()
        onNodeWithTag(PASTE_URL_CHIP).assertExists().assertIsEnabled()
        onNodeWithTag(OPEN_FOOD_FACTS_CHIP).assertExists().assertIsEnabled()
    }
}
