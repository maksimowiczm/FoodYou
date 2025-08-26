package com.maksimowiczm.foodyou.feature.food.diary.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealQuery
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.application.event.subscribe
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.business.shared.domain.fooddiary.FoodDiaryEntryCreatedDomainEvent
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
internal class DiaryFoodSearchViewModel(mealId: Long, queryBus: QueryBus, eventBus: EventBus) :
    ViewModel() {
    val meal =
        queryBus
            .dispatch(ObserveMealQuery(mealId))
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
        eventBus.subscribe<FoodDiaryEntryCreatedDomainEvent>(viewModelScope) {
            eventChannel.send(Unit)
        }
    }
}
