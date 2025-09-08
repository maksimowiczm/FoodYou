package com.maksimowiczm.foodyou.shared.domain.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <reified E : DomainEvent> EventBus.subscribe(
    coroutineScope: CoroutineScope,
    eventHandler: EventHandler<E>,
): Job = events.filterIsInstance<E>().onEach(eventHandler::handle).launchIn(coroutineScope)
