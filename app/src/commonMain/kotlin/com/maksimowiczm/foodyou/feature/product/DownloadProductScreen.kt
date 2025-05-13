package com.maksimowiczm.foodyou.feature.product

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreen
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadProductScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    DownloadProductScreen(
        onBack = onBack,
        modifier = modifier,
        viewModel = koinViewModel()
    )
}

@Composable
private fun DownloadProductScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadProductScreenViewModel = koinViewModel()
) {
    val isMutating = viewModel.isMutating.collectAsStateWithLifecycle().value
    val textFieldState = rememberTextFieldState()

    DownloadProductScreen(
        isMutating = isMutating,
        textFieldState = textFieldState,
        onBack = onBack,
        onDownload = { viewModel.onDownload(textFieldState.text.toString()) },
        modifier = modifier
    )
}