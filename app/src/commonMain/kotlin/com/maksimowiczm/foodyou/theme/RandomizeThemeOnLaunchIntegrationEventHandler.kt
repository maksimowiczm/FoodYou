package com.maksimowiczm.foodyou.theme

import com.maksimowiczm.foodyou.common.domain.event.IntegrationEventHandler
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.common.domain.userpreferences.get
import com.maksimowiczm.foodyou.settings.domain.event.AppLaunchEvent

internal class RandomizeThemeOnLaunchIntegrationEventHandler(
    private val themeSettingsRepository: UserPreferencesRepository<ThemeSettings>,
    private val randomizeThemeUseCase: RandomizeThemeUseCase,
) : IntegrationEventHandler<AppLaunchEvent> {
    override suspend fun handle(event: AppLaunchEvent) {
        if (themeSettingsRepository.get().randomizeOnLaunch) {
            randomizeThemeUseCase.randomize()
        }
    }
}
