package com.maksimowiczm.foodyou.feature.productredesign.ui

internal sealed interface ProductFormEvent {
    data object DownloadedProductSuccessfully : ProductFormEvent
    data object CreatingProduct : ProductFormEvent
    data class ProductCreated(val id: Long) : ProductFormEvent
}
