package com.maksimowiczm.foodyou.feature.productredesign.ui.update

internal sealed interface ProductFormEvent {
    data object UpdatingProduct : ProductFormEvent
    data class ProductUpdated(val id: Long) : ProductFormEvent
}
