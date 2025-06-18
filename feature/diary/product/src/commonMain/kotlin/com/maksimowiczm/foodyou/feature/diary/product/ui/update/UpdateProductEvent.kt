package com.maksimowiczm.foodyou.feature.diary.product.ui.update

internal sealed interface UpdateProductEvent {
    data object Updated : UpdateProductEvent
}
