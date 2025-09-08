package com.maksimowiczm.foodyou.business.settings.domain

import com.maksimowiczm.foodyou.shared.domain.event.EventHandler
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalTime::class)
internal class AppLaunchEventHandler(
    private val settingsRepository: UserPreferencesRepository<Settings>,
    private val observeChangelogUseCase: ObserveChangelogUseCase,
) : EventHandler<AppLaunchEvent> {
    override suspend fun handle(event: AppLaunchEvent) {
        val changelog = observeChangelogUseCase.observe().first()

        settingsRepository.update {
            // If the app version has changed since the last launch, we want reset the preview
            // dialog
            // hidden state, so it can be shown again for the new version if it's a preview.
            val hidePreviewDialog =
                if (lastRememberedVersion != changelog.currentVersion?.version) false
                else this.hidePreviewDialog

            // Update app launch info
            val appLaunchInfo = appLaunchInfo.update(event, changelog)

            copy(appLaunchInfo = appLaunchInfo, hidePreviewDialog = hidePreviewDialog)
        }
    }

    private fun AppLaunchInfo.update(event: AppLaunchEvent, changelog: Changelog): AppLaunchInfo {
        val currentVersion = changelog.currentVersion?.version
        val firstLaunchCurrentVersion =
            if (currentVersion != null && firstLaunchCurrentVersion?.first != currentVersion) {
                currentVersion to event.timestamp
            } else {
                firstLaunchCurrentVersion
            }

        return copy(
            firstLaunch = firstLaunch ?: event.timestamp,
            firstLaunchCurrentVersion = firstLaunchCurrentVersion,
            launchesCount = launchesCount + 1,
        )
    }
}
