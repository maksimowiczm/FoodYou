package com.maksimowiczm.foodyou.feature.food.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.DataStoreUserPreference

class UseUSDA(dataStore: DataStore<Preferences>) :
    DataStoreUserPreference<Boolean, Boolean>(
        dataStore = dataStore,
        key = booleanPreferencesKey("food:use_usda")
    ) {

    override fun Boolean?.toValue() = this ?: false
    override fun Boolean.toStore() = this
}
