package com.maksimowiczm.foodyou.feature.about.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.NullableDataStoreUserPreference

internal class LatestRememberedVersion(dataStore: DataStore<Preferences>) :
    NullableDataStoreUserPreference<String, String>(
        dataStore = dataStore,
        key = stringPreferencesKey("about:latest_remembered_version")
    ) {
    override fun String?.toValue(): String? = this

    override fun String?.toStore(): String? = this
}
