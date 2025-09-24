package com.maksimowiczm.foodyou.app.ui.food.diary.update

internal sealed interface UpdateEntryEvent {
    data object Saved : UpdateEntryEvent
}
