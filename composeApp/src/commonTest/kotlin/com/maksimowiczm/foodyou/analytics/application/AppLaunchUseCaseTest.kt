package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.Analytics
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsRepository
import com.maksimowiczm.foodyou.analytics.domain.FakeAnalyticsRepository
import com.maksimowiczm.foodyou.common.application.testAppConfig
import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.event.ChannelEventBus
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

class AppLaunchUseCaseTest {
    @Test
    fun execute() = runTest {
        var wasCalled = false
        val useCase =
            appLaunchUseCase(
                analyticsRepository =
                    FakeAnalyticsRepository(
                        onLoad = {
                            wasCalled = true
                            Analytics.of()
                        }
                    )
            )

        useCase.execute()
        advanceUntilIdle()
        assertTrue(wasCalled, "Account was never loaded")
    }

    @Test
    fun execute_shouldSaveLoadedAccount() = runTest {
        val testAccount = Analytics.of()
        var wasCalled = false
        val useCase =
            appLaunchUseCase(
                analyticsRepository =
                    FakeAnalyticsRepository(
                        onLoad = { testAccount },
                        onSave = { account ->
                            wasCalled = true
                            assertEquals(testAccount, account, "Account should be saved")
                        },
                    )
            )

        useCase.execute()
        advanceUntilIdle()
        assertTrue(wasCalled, "Account was never saved")
    }

    @Test
    fun execute_shouldLoadBeforeSave() = runTest {
        val channel = Channel<String>(Channel.UNLIMITED)

        val useCase =
            appLaunchUseCase(
                analyticsRepository =
                    FakeAnalyticsRepository(
                        onLoad = {
                            channel.send("load")
                            Analytics.of()
                        },
                        onSave = { channel.send("save") },
                    )
            )

        useCase.execute()
        channel.close()
        advanceUntilIdle()

        val operations = channel.receiveAsFlow().toList()
        assertEquals(listOf("load", "save"), operations, "Load should happen before save")
    }

    @Test
    fun execute_shouldPublishAccountDomainEvents() = runTest {
        val account = Analytics.of()
        val eventBus = ChannelEventBus<DomainEvent>()
        val useCase =
            appLaunchUseCase(
                analyticsRepository = FakeAnalyticsRepository(onLoad = { account }),
                eventBus = eventBus,
            )

        useCase.execute()
        eventBus.close()
        advanceUntilIdle()

        val events = eventBus.events.toList()
        assertEquals(account.events, events, "Account events should be published")
    }

    fun appLaunchUseCase(
        analyticsRepository: AnalyticsRepository = FakeAnalyticsRepository(),
        eventBus: EventBus<DomainEvent> = ChannelEventBus(),
    ) =
        AppLaunchUseCase(
            clock = staticClock(),
            analyticsRepository = analyticsRepository,
            appConfig = testAppConfig(),
            eventBus = eventBus,
        )
}
