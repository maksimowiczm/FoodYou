package com.maksimowiczm.foodyou.feature.product

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProduct
import com.maksimowiczm.foodyou.feature.product.ui.download.DownloadProductScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DownloadProductScreen(
    text: String?,
    onBack: () -> Unit,
    onDownload: (RemoteProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    DownloadProductScreenImpl(
        text = text,
        onBack = onBack,
        onDownload = onDownload,
        modifier = modifier
    )
}

@Composable
internal expect fun DownloadProductScreenImpl(
    text: String?,
    onBack: () -> Unit,
    onDownload: (RemoteProduct) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadProductScreenViewModel = koinViewModel(
        parameters = { parametersOf(text) }
    )
)
