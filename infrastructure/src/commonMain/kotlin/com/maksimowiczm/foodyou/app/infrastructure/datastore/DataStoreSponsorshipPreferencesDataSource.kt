package com.maksimowiczm.foodyou.app.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreSponsorshipPreferencesDataSource(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository<SponsorshipPreferences> {
    override fun observe(): Flow<SponsorshipPreferences> =
        dataStore.data.map(Preferences::toSponsorshipPreferences)

    override suspend fun update(transform: SponsorshipPreferences.() -> SponsorshipPreferences) {
        dataStore.updateData { currentPreferences ->
            val current = currentPreferences.toSponsorshipPreferences()
            val updated = current.transform()
            currentPreferences.toMutablePreferences().applySponsorshipPreferences(updated)
        }
    }
}

private fun Preferences.toSponsorshipPreferences(): SponsorshipPreferences =
    SponsorshipPreferences(
        remoteAllowed = this[SponsorshipPreferencesKeys.allowRemoteSponsorships] ?: false
    )

private fun MutablePreferences.applySponsorshipPreferences(
    sponsorshipPreferences: SponsorshipPreferences
): MutablePreferences = apply {
    this[SponsorshipPreferencesKeys.allowRemoteSponsorships] = sponsorshipPreferences.remoteAllowed
}

private object SponsorshipPreferencesKeys {
    val allowRemoteSponsorships = booleanPreferencesKey("sponsorship:allow_remote_sponsorships")
}
