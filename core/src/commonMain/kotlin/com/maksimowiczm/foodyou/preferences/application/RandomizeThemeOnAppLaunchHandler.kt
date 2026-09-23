package com.maksimowiczm.foodyou.preferences.application

import com.maksimowiczm.foodyou.analytics.domain.AppLaunchedEvent
import com.maksimowiczm.foodyou.common.event.EventHandler
import com.maksimowiczm.foodyou.preferences.domain.ThemePreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.random
import com.maksimowiczm.foodyou.preferences.domain.update

/** Handles theme randomization when the application is launched. */
class RandomizeThemeOnAppLaunchHandler(
    private val preferencesRepository: UserPreferencesRepository
) : EventHandler<AppLaunchedEvent> {
    override suspend fun handle(event: AppLaunchedEvent) {
        preferencesRepository.update<ThemePreference> {
            if (it.randomizeOnLaunch) it.copy(theme = it.theme.random()) else it
        }
    }
}
