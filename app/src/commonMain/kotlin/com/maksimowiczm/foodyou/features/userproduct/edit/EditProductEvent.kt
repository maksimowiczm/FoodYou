package com.maksimowiczm.foodyou.features.userproduct.edit

internal sealed interface EditProductEvent {
    data object Edited : EditProductEvent
}
