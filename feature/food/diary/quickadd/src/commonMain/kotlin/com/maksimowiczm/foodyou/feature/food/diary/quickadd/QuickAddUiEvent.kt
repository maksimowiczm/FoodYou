package com.maksimowiczm.foodyou.feature.food.diary.quickadd

internal sealed interface QuickAddUiEvent {
    data object Saved : QuickAddUiEvent
}
