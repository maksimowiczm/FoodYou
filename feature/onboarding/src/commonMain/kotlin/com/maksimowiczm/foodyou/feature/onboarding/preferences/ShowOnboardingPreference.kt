package com.maksimowiczm.foodyou.feature.onboarding.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

class ShowOnboardingPreference(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, Boolean>(
        dataStore = dataStore,
        key = booleanPreferencesKey("onboarding:show_onboarding")
    ) {
    override fun Boolean?.toValue(): Boolean = this ?: true
    override fun Boolean.toStore(): Boolean? = this
}
