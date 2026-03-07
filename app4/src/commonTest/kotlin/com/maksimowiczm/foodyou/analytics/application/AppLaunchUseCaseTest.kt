package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.analytics.domain.FakeAccountAnalyticsRepository
import com.maksimowiczm.foodyou.analytics.domain.testAccountAnalytics
import com.maksimowiczm.foodyou.app.domain.testAppConfig
import com.maksimowiczm.foodyou.common.clock.testClock
import com.maksimowiczm.foodyou.common.domain.testLocalAccountId
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
    fun execute_shouldLoadAccountWithGivenId() = runTest {
        val testId = testLocalAccountId()
        var wasCalled = false
        val useCase =
            appLaunchUseCase(
                accountAnalyticsRepository =
                    FakeAccountAnalyticsRepository(
                        onLoad = { id ->
                            wasCalled = true
                            assertEquals(testId, id, "Account ID should be loaded")
                            testAccountAnalytics()
                        }
                    )
            )

        useCase.execute(testId)
        advanceUntilIdle()
        assertTrue(wasCalled, "Account was never loaded")
    }

    @Test
    fun execute_shouldSaveLoadedAccount() = runTest {
        val testAccount = testAccountAnalytics()
        var wasCalled = false
        val useCase =
            appLaunchUseCase(
                accountAnalyticsRepository =
                    FakeAccountAnalyticsRepository(
                        onLoad = { testAccount },
                        onSave = { account ->
                            wasCalled = true
                            assertEquals(testAccount, account, "Account should be saved")
                        },
                    )
            )

        useCase.execute(testAccount.ownerId)
        advanceUntilIdle()
        assertTrue(wasCalled, "Account was never saved")
    }

    @Test
    fun execute_shouldLoadBeforeSave() = runTest {
        val channel = Channel<String>(Channel.UNLIMITED)

        val useCase =
            appLaunchUseCase(
                accountAnalyticsRepository =
                    FakeAccountAnalyticsRepository(
                        onLoad = {
                            channel.send("load")
                            testAccountAnalytics()
                        },
                        onSave = { channel.send("save") },
                    )
            )

        useCase.execute(testLocalAccountId())
        channel.close()
        advanceUntilIdle()

        val operations = channel.receiveAsFlow().toList()
        assertEquals(listOf("load", "save"), operations, "Load should happen before save")
    }

    @Test
    fun execute_shouldPublishAccountDomainEvents() = runTest {
        val account = testAccountAnalytics()
        val eventBus = ChannelEventBus<DomainEvent>()
        val useCase =
            appLaunchUseCase(
                accountAnalyticsRepository = FakeAccountAnalyticsRepository(onLoad = { account }),
                eventBus = eventBus,
            )

        useCase.execute(account.ownerId)
        eventBus.close()
        advanceUntilIdle()

        val events = eventBus.events.toList()
        assertEquals(account.events, events, "Account events should be published")
    }

    fun appLaunchUseCase(
        accountAnalyticsRepository: AccountAnalyticsRepository = FakeAccountAnalyticsRepository(),
        eventBus: EventBus<DomainEvent> = ChannelEventBus(),
    ) =
        AppLaunchUseCase(
            clock = testClock(),
            accountAnalyticsRepository = accountAnalyticsRepository,
            appConfig = testAppConfig(),
            eventBus = eventBus,
        )
}
