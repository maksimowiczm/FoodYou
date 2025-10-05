package com.maksimowiczm.foodyou.analytics.application

import com.maksimowiczm.foodyou.analytics.domain.AccountAnalyticsRepository
import com.maksimowiczm.foodyou.analytics.domain.AppLaunchedEvent
import com.maksimowiczm.foodyou.common.domain.Err
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.Ok
import com.maksimowiczm.foodyou.common.domain.Result
import com.maksimowiczm.foodyou.common.domain.event.EventBus
import kotlin.time.Clock

/**
 * Command to record an app launch for a specific account and app version.
 *
 * @property localAccountId The ID of the account launching the app.
 * @property versionName The version name of the app being launched.
 */
data class AppLaunchCommand(val localAccountId: LocalAccountId, val versionName: String) {
    sealed interface Error {
        data object AccountNotFound : Error
    }
}

class AppLaunchCommandHandler(
    private val clock: Clock,
    private val accountAnalyticsRepository: AccountAnalyticsRepository,
    private val eventBus: EventBus,
) {
    suspend fun execute(command: AppLaunchCommand): Result<Unit, AppLaunchCommand.Error> {
        val account =
            accountAnalyticsRepository.load(command.localAccountId)
                ?: return Err(AppLaunchCommand.Error.AccountNotFound)

        account.recordAppLaunch(versionName = command.versionName, clock = clock)

        accountAnalyticsRepository.save(account)

        eventBus.publish(
            AppLaunchedEvent(versionName = command.versionName, timestamp = clock.now())
        )

        return Ok()
    }
}
