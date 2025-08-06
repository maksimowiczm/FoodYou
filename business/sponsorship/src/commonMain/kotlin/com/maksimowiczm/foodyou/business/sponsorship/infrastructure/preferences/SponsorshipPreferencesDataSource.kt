package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences

import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorshipPreferences
import kotlinx.coroutines.flow.Flow

internal interface SponsorshipPreferencesDataSource {
    fun observe(): Flow<SponsorshipPreferences>

    suspend fun update(preferences: SponsorshipPreferences)
}
