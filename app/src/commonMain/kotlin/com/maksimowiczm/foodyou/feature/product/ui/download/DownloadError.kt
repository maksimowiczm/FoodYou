package com.maksimowiczm.foodyou.feature.product.ui.download

internal sealed interface DownloadError {
    data object URLNotFound : DownloadError
    data object URLNotSupported : DownloadError
    data object ProductNotFound : DownloadError
    data class Custom(val message: String?) : DownloadError
}
