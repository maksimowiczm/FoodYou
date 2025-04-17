package com.maksimowiczm.foodyou.feature.productredesign.ui

internal sealed interface ProductFormEvent {
    data object DownloadedProductSuccessfully : ProductFormEvent
}
