package com.maksimowiczm.foodyou.feature.food.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.core.preferences.NullableDataStoreUserPreference

internal class UsdaApiKey(dataStore: DataStore<Preferences>) :
    NullableDataStoreUserPreference<String, String>(
        dataStore = dataStore,
        key = stringPreferencesKey("food:use_usda_api")
    ) {

    override fun String?.toValue(): String? = this
    override fun String?.toStore(): String? = this
}
