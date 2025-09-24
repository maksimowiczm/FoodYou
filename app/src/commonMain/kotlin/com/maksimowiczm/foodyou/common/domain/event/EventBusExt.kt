package com.maksimowiczm.foodyou.common.domain.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

inline fun <reified E : IntegrationEvent> EventBus.subscribe(
    coroutineScope: CoroutineScope,
    integrationEventHandler: IntegrationEventHandler<E>,
): Job =
    events.filterIsInstance<E>().onEach(integrationEventHandler::handle).launchIn(coroutineScope)
