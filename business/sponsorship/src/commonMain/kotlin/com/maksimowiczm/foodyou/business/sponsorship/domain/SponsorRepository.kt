package com.maksimowiczm.foodyou.business.sponsorship.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface SponsorRepository {
    fun observeSponsorships(allowRemote: Boolean? = null): Flow<PagingData<Sponsorship>>

    fun observeSponsorshipPreferences(): Flow<SponsorshipPreferences>

    suspend fun setSponsorshipPreferences(prefs: SponsorshipPreferences)
}
