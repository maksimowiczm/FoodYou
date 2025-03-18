package com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.product.update

import com.maksimowiczm.foodyou.data.model.Product

sealed interface UpdateProductState {
    sealed interface WithProduct : UpdateProductState {
        val product: Product
    }

    data object Loading : UpdateProductState
    data class ProductReady(override val product: Product) : WithProduct
    data class UpdatingProduct(override val product: Product) : WithProduct
    data class ProductUpdated(override val product: Product) : WithProduct
}
