package com.maksimowiczm.foodyou.feature.product.ui.create

internal sealed interface ProductFormEvent {
    data object DownloadedProductSuccessfully : ProductFormEvent
    data object CreatingProduct : ProductFormEvent
    data class ProductCreated(val id: Long) : ProductFormEvent
}
