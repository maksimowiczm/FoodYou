package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.Event
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.event.EventHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import com.maksimowiczm.foodyou.shared.common.infrastructure.command.InMemoryCommandBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.date.DateProviderImpl
import com.maksimowiczm.foodyou.shared.common.infrastructure.event.InMemoryEventBus
import com.maksimowiczm.foodyou.shared.common.infrastructure.query.InMemoryQueryBus
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

expect fun Module.systemDetails()

val sharedCommonModule = module {
    @Suppress("UNCHECKED_CAST")
    single {
            val handlers = getAll<CommandHandler<*, *, *>>()
            InMemoryCommandBus(handlers as List<CommandHandler<Command, *, *>>)
        }
        .bind<CommandBus>()

    @Suppress("UNCHECKED_CAST")
    single {
            val handlers = getAll<QueryHandler<*, *>>()
            InMemoryQueryBus(handlers as List<QueryHandler<Query, *>>)
        }
        .bind<QueryBus>()

    single(named("EventBusScope")) {
            CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("EventBusScope"))
        }
        .onClose { it?.cancel() }

    @Suppress("UNCHECKED_CAST")
    single {
            val scope = get<CoroutineScope>(named("EventBusScope"))
            val handlers = getAll<EventHandler<*>>()
            InMemoryEventBus(handlers as List<EventHandler<Event>>, scope)
        }
        .bind<EventBus>()

    singleOf(::DateProviderImpl).bind<DateProvider>()

    systemDetails()
}
