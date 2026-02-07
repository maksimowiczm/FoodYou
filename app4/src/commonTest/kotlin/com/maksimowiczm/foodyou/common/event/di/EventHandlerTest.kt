package com.maksimowiczm.foodyou.common.event.di

import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.ChannelEventBus
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.EventHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.definition.Kind
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

@OptIn(KoinInternalApi::class)
class EventHandlerTest {
    @Test
    fun should_register_multiple_handlers_for_the_same_event() = runTest {
        val firstQualifier = qualifier("First")
        val secondQualifier = qualifier("Second")
        val eventBusQualifier = qualifier(DomainEvent::class.qualifiedName!!)
        val testModule = module {
            configureTestModule(eventBusQualifier)
            eventHandler(eventBusQualifier, firstQualifier) { TestHandler() }
            eventHandler(eventBusQualifier, secondQualifier) { TestHandler() }
        }

        executeInKoinContext(testModule) {
            val handlerInstances =
                instanceRegistry.instances.filterValues {
                    it.beanDefinition.qualifier == firstQualifier ||
                        it.beanDefinition.qualifier == secondQualifier
                }

            assertEquals(2, handlerInstances.size, "Expected 2 handler instances")
        }
    }

    @Test
    fun should_start_event_handler_at_creation() = runTest {
        val qualifier = qualifier(TestHandler::class.qualifiedName!!)
        val eventBusQualifier = qualifier(DomainEvent::class.qualifiedName!!)
        val testModule = module {
            configureTestModule(eventBusQualifier)
            eventHandler(eventBusQualifier, qualifier) { TestHandler() }
        }

        executeInKoinContext(testModule) {
            val handlerInstance =
                instanceRegistry.instances.values.toList().single {
                    it.beanDefinition.qualifier == qualifier
                }

            assertEquals(Kind.Singleton, handlerInstance.beanDefinition.kind)
            assertTrue { handlerInstance.isCreated() }
        }
    }

    @Test
    fun should_cancel_handler_subscriptions_when_scope_is_closed() = runTest {
        val qualifier = qualifier(TestHandler::class.qualifiedName!!)
        val eventBusQualifier = qualifier(DomainEvent::class.qualifiedName!!)
        val testModule = module {
            configureTestModule(eventBusQualifier)
            eventHandler(eventBusQualifier, qualifier) { TestHandler() }
        }

        executeInKoinContext(testModule) {
            val job = get<Job>(qualifier)
            assertFalse(job.isCancelled, "Job should not be cancelled")
            close()
            assertTrue(job.isCancelled, "Job should be cancelled")
        }
    }

    private suspend fun executeInKoinContext(
        vararg modules: Module,
        block: suspend Koin.() -> Unit,
    ) {
        try {
            // It's possible that koin is already running. On Android it will be already running
            stopKoin()
        } finally {
            val koin = startKoin { modules(*modules) }
            try {
                block(koin.koin)
                koin.close()
            } finally {
                stopKoin()
            }
        }
    }

    context(scope: TestScope)
    private fun Module.configureTestModule(eventBusQualifier: Qualifier) {
        applicationCoroutineScope { scope.backgroundScope }
        single<EventBus<DomainEvent>>(eventBusQualifier) { ChannelEventBus() }
    }

    private class TestHandler : EventHandler<DomainEvent> {
        override suspend fun handle(event: DomainEvent) = Unit
    }
}
