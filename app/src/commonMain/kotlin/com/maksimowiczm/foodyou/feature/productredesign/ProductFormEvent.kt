package com.maksimowiczm.foodyou.feature.productredesign

internal sealed interface ProductFormEvent {
    data object DownloadedProductSuccessfully : ProductFormEvent
}
