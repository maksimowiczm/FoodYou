package com.maksimowiczm.foodyou.app.infrastructure.shared.event

import com.maksimowiczm.foodyou.shared.domain.event.EventBus
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal fun Module.eventModule() {
    singleOf(::SharedFlowEventBus).bind<EventBus>()
}
