package com.maksimowiczm.foodyou.sponsorship.domain.event

import com.maksimowiczm.foodyou.common.domain.event.IntegrationEventHandler
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.common.domain.userpreferences.get
import com.maksimowiczm.foodyou.settings.domain.event.AppLaunchEvent
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository

class AppLaunchEventHandler(
    private val sponsorshipPreferencesRepository: UserPreferencesRepository<SponsorshipPreferences>,
    private val sponsorshipRepository: SponsorRepository,
) : IntegrationEventHandler<AppLaunchEvent> {
    override suspend fun handle(event: AppLaunchEvent) {
        val prefs = sponsorshipPreferencesRepository.get()
        if (prefs.shouldCleanLegacyEntities) {
            sponsorshipRepository.deleteAll()
            sponsorshipPreferencesRepository.update { copy(shouldCleanLegacyEntities = false) }
        }
    }
}
