package com.maksimowiczm.foodyou.app.ui.food.diary.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.event.EventBus
import com.maksimowiczm.foodyou.common.domain.event.subscribe
import com.maksimowiczm.foodyou.fooddiary.domain.event.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

internal class DiaryFoodSearchViewModel(
    mealId: Long,
    eventBus: EventBus,
    mealRepository: MealRepository,
) : ViewModel() {
    val meal =
        mealRepository
            .observeMeal(mealId)
            .map { if (it == null) null else MealModel(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    // Use channel to emit events because it will wait for consumer. We can't directly use event bus
    // because it will emit events even if no one is listening.
    private val eventChannel = Channel<Unit>()
    val newEntryEvents = eventChannel.receiveAsFlow()

    init {
        eventBus.subscribe<FoodDiaryEntryCreatedEvent>(viewModelScope) { eventChannel.send(Unit) }
    }
}
