package com.maksimowiczm.foodyou.app.ui.product.edit

sealed interface EditProductEvent {
    data object Edited : EditProductEvent
}
