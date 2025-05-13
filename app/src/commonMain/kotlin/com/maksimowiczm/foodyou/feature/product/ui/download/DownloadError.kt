package com.maksimowiczm.foodyou.feature.product.ui.download

sealed interface DownloadError {
    data object URLNotFound : DownloadError
    data object URLNotSupported : DownloadError
    data class Custom(val message: String) : DownloadError
}
