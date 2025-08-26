package com.maksimowiczm.foodyou.business.shared.application.event

import com.maksimowiczm.foodyou.business.shared.domain.event.DomainEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <reified E : DomainEvent> EventBus.subscribe(
    coroutineScope: CoroutineScope,
    eventHandler: EventHandler<E>,
): Job = events.filterIsInstance<E>().onEach(eventHandler::handle).launchIn(coroutineScope)
