package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.event.EventBus
import kotlin.time.Clock

/**
 * Command to record an app launch for a specific account and app version.
 *
 * @property localAccountId The ID of the account launching the app.
 * @property versionName The version name of the app being launched.
 */
data class AppLaunchCommand(val localAccountId: LocalAccountId, val versionName: String)

class AppLaunchCommandHandler(
    private val clock: Clock,
    private val accountAnalyticsRepository: AccountAnalyticsRepository,
    private val eventBus: EventBus,
) {
    suspend fun handle(command: AppLaunchCommand): Result<Unit, Unit> {
        val account = accountAnalyticsRepository.load(command.localAccountId)

        account.recordAppLaunch(versionName = command.versionName, clock = clock)

        accountAnalyticsRepository.save(account)

        eventBus.publish(account.events)

        return Ok()
    }
}
