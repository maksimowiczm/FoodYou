package com.maksimowiczm.foodyou.feature.product.ui.download

import com.maksimowiczm.foodyou.feature.product.data.network.usda.USDAException

internal sealed interface DownloadError {
    data object URLNotFound : DownloadError
    data object URLNotSupported : DownloadError
    data object ProductNotFound : DownloadError
    data class Custom(val message: String?) : DownloadError
    data class UsdaApiKeyError(val error: USDAException) : DownloadError
}
