package com.maksimowiczm.foodyou.feature.product.ui.download

import com.maksimowiczm.foodyou.feature.product.data.network.usda.USDAException

internal sealed interface DownloadError {
    sealed interface GenericError : DownloadError {
        data object URLNotFound : GenericError
        data object URLNotSupported : GenericError
        data object ProductNotFound : GenericError
        data class Custom(val message: String?) : GenericError
    }

    data class UsdaApiKeyError(val error: USDAException) : DownloadError
}
