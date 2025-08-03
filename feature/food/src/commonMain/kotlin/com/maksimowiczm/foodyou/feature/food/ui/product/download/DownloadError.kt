package com.maksimowiczm.foodyou.feature.food.ui.product.download

import com.maksimowiczm.foodyou.feature.usda.USDAException

internal sealed interface DownloadError {
    sealed interface GenericError : DownloadError {
        data object URLNotFound : GenericError
        data object URLNotSupported : GenericError
        data object ProductNotFound : GenericError
        data class Custom(val message: String?) : GenericError
    }

    data class Usda(val error: USDAException) : DownloadError
}
