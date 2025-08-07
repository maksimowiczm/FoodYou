package com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DataStoreSettingsDataSource(private val dataStore: DataStore<Preferences>) :
    LocalSettingsDataSource {

    override fun observe(): Flow<Settings> = dataStore.data.map { preferences -> Settings() }

    override suspend fun update(settings: Settings) {
        dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {}
        }
    }
}

private object SettingsPreferencesKeys {}
