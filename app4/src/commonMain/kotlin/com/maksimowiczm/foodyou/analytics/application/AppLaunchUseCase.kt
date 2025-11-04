package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.app.domain.AppConfig
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.event.DomainEvent
import com.maksimowiczm.foodyou.common.event.EventBus
import kotlin.time.Clock

/** Use case to record an app launch for a specific account and app version. */
class AppLaunchUseCase(
    private val clock: Clock,
    private val accountAnalyticsRepository: AccountAnalyticsRepository,
    private val appConfig: AppConfig,
    private val eventBus: EventBus<DomainEvent>,
) {
    suspend fun execute(localAccountId: LocalAccountId) {
        val account = accountAnalyticsRepository.load(localAccountId)

        account.recordAppLaunch(versionName = appConfig.versionName, clock = clock)

        accountAnalyticsRepository.save(account)

        eventBus.publish(account.events)
    }
}
