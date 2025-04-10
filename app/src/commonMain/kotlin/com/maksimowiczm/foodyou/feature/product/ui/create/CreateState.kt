package com.maksimowiczm.foodyou.feature.product.ui.create

internal sealed interface CreateState {
    data object Nothing : CreateState
    data object CreatingProduct : CreateState

    @JvmInline
    value class Created(val productId: Long) : CreateState
}
