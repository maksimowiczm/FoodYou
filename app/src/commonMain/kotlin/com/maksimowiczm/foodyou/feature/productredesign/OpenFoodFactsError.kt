package com.maksimowiczm.foodyou.feature.productredesign

internal sealed interface OpenFoodFactsError {
    data object InvalidUrl : OpenFoodFactsError
    data class DownloadProductFailed(val error: Throwable) : OpenFoodFactsError
}
