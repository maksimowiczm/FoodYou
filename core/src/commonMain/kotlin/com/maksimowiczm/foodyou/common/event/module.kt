package com.maksimowiczm.foodyou.common.event

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

fun Module.inMemoryEventBus() {
    singleOf(::InMemoryEventBus).bind<EventBus>()
}
