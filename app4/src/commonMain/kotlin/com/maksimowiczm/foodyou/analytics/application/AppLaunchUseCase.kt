package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import kotlin.time.Clock

/**
 * Use case for recording application launch events in the analytics system.
 *
 * This use case is responsible for tracking when users launch the application, capturing relevant
 * metadata such as the app version, and persisting this information for analytics purposes.
 */
class AppLaunchUseCase(
    private val clock: Clock,
    private val accountAnalyticsRepository: AccountAnalyticsRepository,
    private val appConfig: AppConfig,
    private val eventBus: EventBus<DomainEvent>,
) {
    /**
     * Records an app launch event for the specified account.
     *
     * This method:
     * 1. Loads the account's analytics data
     * 2. Records the app launch with current version information
     * 3. Persists the updated analytics data
     * 4. Publishes any domain events generated during the process
     *
     * @param localAccountId The unique identifier of the account launching the app
     */
    suspend fun execute(localAccountId: LocalAccountId) {
        val account = accountAnalyticsRepository.load(localAccountId)
        account.recordAppLaunch(versionName = appConfig.versionName, clock = clock)
        accountAnalyticsRepository.save(account)
        eventBus.publish(account.events)
    }
}
