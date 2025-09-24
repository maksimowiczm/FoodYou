package com.maksimowiczm.foodyou.app.ui.food.diary.add

internal sealed interface AddEntryEvent {
    data object FoodDeleted : AddEntryEvent

    data object EntryAdded : AddEntryEvent
}
