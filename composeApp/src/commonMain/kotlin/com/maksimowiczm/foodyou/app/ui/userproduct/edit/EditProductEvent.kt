package com.maksimowiczm.foodyou.app.ui.userproduct.edit

internal sealed interface EditProductEvent {
    data object Edited : EditProductEvent
}
