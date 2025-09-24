package com.maksimowiczm.foodyou.common.infrastructure.inmemory

import com.maksimowiczm.foodyou.common.domain.event.EventBus
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

fun Module.inMemoryModule() {
    singleOf(::SharedFlowEventBus).bind<EventBus>()
}
