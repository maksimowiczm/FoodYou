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
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.FAB
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenTestTags.OPEN_FOOD_FACTS_CHIP
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
        textFieldState: TextFieldState = rememberTextFieldState(),
        onBack: () -> Unit = {},
        onDownload: () -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreen(
            isMutating = isMutating,
            textFieldState = textFieldState,
            onBack = onBack,
            onDownload = onDownload,
            modifier = modifier
        )
    }

    @Test
    fun idleState() = runComposeUiTest {
        setContent {
            DownloadProductScreen()
        }

        onNodeWithTag(PROGRESS_INDICATOR).assertDoesNotExist()
        onNodeWithTag(TEXT_FIELD).assertIsDisplayed()
        onNodeWithTag(FAB).assertIsDisplayed()
        onNodeWithTag(PASTE_URL_CHIP).assertExists().assertIsEnabled()
        onNodeWithTag(OPEN_FOOD_FACTS_CHIP).assertExists().assertIsEnabled()
    }

    @Test
    fun loadingState() = runComposeUiTest {
        setContent {
            DownloadProductScreen(isMutating = true)
        }

        onNodeWithTag(PROGRESS_INDICATOR).assertIsDisplayed()
        onNodeWithTag(TEXT_FIELD).assertExists().assertIsNotEnabled()
        onNodeWithTag(FAB).assertIsNotDisplayed()
        onNodeWithTag(PASTE_URL_CHIP).assertExists().assertIsNotEnabled()
        onNodeWithTag(OPEN_FOOD_FACTS_CHIP).assertExists().assertIsEnabled()
    }
}
