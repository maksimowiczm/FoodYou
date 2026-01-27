package com.maksimowiczm.foodyou.app.ui.userfood.edit

sealed interface EditProductEvent {
    data object Edited : EditProductEvent
}
