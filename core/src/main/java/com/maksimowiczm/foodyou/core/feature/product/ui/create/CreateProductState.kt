package com.maksimowiczm.foodyou.core.feature.product.ui.create

sealed interface CreateProductState {
    data object Nothing : CreateProductState
    data object CreatingProduct : CreateProductState
    data class ProductCreated(
        val productId: Long
    ) : CreateProductState
}
