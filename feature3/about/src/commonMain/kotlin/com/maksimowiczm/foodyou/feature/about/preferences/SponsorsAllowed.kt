package com.maksimowiczm.foodyou.feature.about.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

internal class SponsorsAllowed(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, Boolean>(
        dataStore = dataStore,
        key = booleanPreferencesKey("about:sponsors_allowed")
    ) {

    override fun Boolean?.toValue(): Boolean = this ?: false
    override fun Boolean.toStore(): Boolean? = this
}
