package com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorshipPreferences
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.SponsorshipPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreSponsorshipPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) : SponsorshipPreferencesDataSource {

    override fun observe(): Flow<SponsorshipPreferences> =
        dataStore.data.map { preferences ->
            SponsorshipPreferences(
                remoteAllowed =
                    preferences[SponsorshipPreferencesKeys.allowRemoteSponsorships] ?: true
            )
        }

    override suspend fun update(preferences: SponsorshipPreferences) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                set(SponsorshipPreferencesKeys.allowRemoteSponsorships, preferences.remoteAllowed)
            }
        }
    }
}

private object SponsorshipPreferencesKeys {
    val allowRemoteSponsorships = booleanPreferencesKey("sponsorship:allow_remote_sponsorships")
}
