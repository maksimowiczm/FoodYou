package com.maksimowiczm.foodyou.sponsorship.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.common.infrastructure.datastore.AbstractDataStoreUserPreferencesRepository
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences

internal class DataStoreSponsorshipPreferencesDataSource(dataStore: DataStore<Preferences>) :
    AbstractDataStoreUserPreferencesRepository<SponsorshipPreferences>(dataStore) {
    override fun Preferences.toUserPreferences(): SponsorshipPreferences =
        SponsorshipPreferences(
            remoteAllowed = this[SponsorshipPreferencesKeys.allowRemoteSponsorships] ?: false,
            shouldCleanLegacyEntities = this[SponsorshipPreferencesKeys.cleanLegacyEntities] ?: true,
        )

    override fun MutablePreferences.applyUserPreferences(updated: SponsorshipPreferences) {
        this[SponsorshipPreferencesKeys.allowRemoteSponsorships] = updated.remoteAllowed
        this[SponsorshipPreferencesKeys.cleanLegacyEntities] = updated.shouldCleanLegacyEntities
    }
}

private object SponsorshipPreferencesKeys {
    val allowRemoteSponsorships = booleanPreferencesKey("sponsorship:allow_remote_sponsorships")
    val cleanLegacyEntities = booleanPreferencesKey("sponsorship:clean_legacy_entities")
}
