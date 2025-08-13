package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

internal sealed interface AddEntryEvent {
    data object FoodDeleted : AddEntryEvent

    data object EntryAdded : AddEntryEvent
}
