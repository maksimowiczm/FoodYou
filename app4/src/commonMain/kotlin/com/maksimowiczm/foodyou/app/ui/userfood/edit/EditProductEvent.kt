package com.maksimowiczm.foodyou.app.ui.userfood.edit

internal sealed interface EditProductEvent {
    data object Edited : EditProductEvent
}
