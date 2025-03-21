package com.maksimowiczm.foodyou.feature.diary.ui.product.create

sealed interface CreateProductState {
    data object Nothing : CreateProductState
    data object CreatingProduct : CreateProductState
    data object Error : CreateProductState
    data class ProductCreated(val productId: Long) : CreateProductState
}
