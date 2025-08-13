package com.maksimowiczm.foodyou.feature.food.diary.update.presentation

internal sealed interface UpdateEntryEvent {
    data object Saved : UpdateEntryEvent
}
