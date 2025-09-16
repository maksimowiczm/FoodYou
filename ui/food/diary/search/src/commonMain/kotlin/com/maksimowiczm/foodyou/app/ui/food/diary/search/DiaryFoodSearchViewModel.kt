package com.maksimowiczm.foodyou.app.ui.food.diary.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.app.business.shared.domain.fooddiary.FoodDiaryEntryCreatedEvent
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.shared.domain.event.EventBus
import com.maksimowiczm.foodyou.shared.domain.event.subscribe
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
internal class DiaryFoodSearchViewModel(
    mealId: Long,
    eventBus: EventBus,
    mealRepository: MealRepository,
) : ViewModel() {
    val meal =
        mealRepository
            .observeMeal(mealId)
            .mapIfNotNull(::MealModel)
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
