package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.InMemoryEventBus
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val inMemoryEventBusModule = module { singleOf(::InMemoryEventBus).bind<EventBus>() }
