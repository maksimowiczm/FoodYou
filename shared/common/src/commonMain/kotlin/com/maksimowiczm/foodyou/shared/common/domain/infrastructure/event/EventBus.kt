package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event

import com.maksimowiczm.foodyou.shared.common.infrastructure.event.EventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface EventBus {

    val events: Flow<Event>

    suspend fun publish(event: Event)
}

inline fun <reified E : Event> EventBus.subscribe(
    coroutineScope: CoroutineScope,
    eventHandler: EventHandler<E>,
): Job = events.filterIsInstance<E>().onEach(eventHandler::handle).launchIn(coroutineScope)
