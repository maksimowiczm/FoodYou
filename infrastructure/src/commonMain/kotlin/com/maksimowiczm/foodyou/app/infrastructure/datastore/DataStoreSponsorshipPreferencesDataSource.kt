package com.maksimowiczm.foodyou.app.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences

internal class DataStoreSponsorshipPreferencesDataSource(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<SponsorshipPreferences>(dataStore) {
    override fun Preferences.toUserPreferences(): SponsorshipPreferences =
        SponsorshipPreferences(
            remoteAllowed = this[SponsorshipPreferencesKeys.allowRemoteSponsorships] ?: false
        )

    override fun MutablePreferences.applyUserPreferences(updated: SponsorshipPreferences) {
        this[SponsorshipPreferencesKeys.allowRemoteSponsorships] = updated.remoteAllowed
    }
}

private object SponsorshipPreferencesKeys {
    val allowRemoteSponsorships = booleanPreferencesKey("sponsorship:allow_remote_sponsorships")
}
