package com.maksimowiczm.foodyou.feature.product

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.maksimowiczm.foodyou.core.ui.utils.LocalClipboardManager
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreen
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenViewModel
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadProductScreen(
    onBack: () -> Unit,
    onDownload: (RemoteProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    DownloadProductScreen(
        onBack = onBack,
        onDownload = onDownload,
        modifier = modifier,
        viewModel = koinViewModel()
    )
}

@Composable
private fun DownloadProductScreen(
    onBack: () -> Unit,
    onDownload: (RemoteProduct) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadProductScreenViewModel = koinViewModel()
) {
    val isMutating = viewModel.isMutating.collectAsStateWithLifecycle().value
    val error = viewModel.error.collectAsStateWithLifecycle().value
    val textFieldState = rememberTextFieldState()

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.productEvent.collect(onDownload)
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current
    val openFoodFactsUrl = stringResource(Res.string.link_open_food_facts)
    val suggestDatabaseUrl = stringResource(Res.string.link_github_issue)

    DownloadProductScreen(
        isMutating = isMutating,
        error = error,
        textFieldState = textFieldState,
        onBack = onBack,
        onDownload = { viewModel.onDownload(textFieldState.text.toString()) },
        onPaste = {
            clipboardManager
                .paste()
                ?.takeIf { it.isNotEmpty() }
                ?.let { textFieldState.setTextAndPlaceCursorAtEnd(it) }
        },
        onOpenFoodFacts = { uriHandler.openUri(openFoodFactsUrl) },
        onSuggestDatabase = { uriHandler.openUri(suggestDatabaseUrl) },
        modifier = modifier
    )
}
