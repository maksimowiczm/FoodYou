package com.maksimowiczm.foodyou.feature.product.ui.update

internal sealed interface UpdateState {
    data object Nothing : UpdateState
    data object UpdatingProduct : UpdateState
    data class Updated(val productId: Long) : UpdateState
}
