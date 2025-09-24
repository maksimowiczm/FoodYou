package com.maksimowiczm.foodyou.app.ui.food.diary.quickadd

internal sealed interface QuickAddUiEvent {
    data object Saved : QuickAddUiEvent
}
