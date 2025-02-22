package com.maksimowiczm.foodyou.core.feature.product.ui.crud.create

sealed interface CreateProductState {
    data object Nothing : CreateProductState
    data object CreatingProduct : CreateProductState
    data object Error : CreateProductState
    data class ProductCreated(val productId: Long) : CreateProductState
}
