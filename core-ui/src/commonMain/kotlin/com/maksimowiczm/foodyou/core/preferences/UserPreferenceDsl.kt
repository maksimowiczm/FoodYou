package com.maksimowiczm.foodyou.core.preferences

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.compose.koinInject

@Composable
inline fun <reified T : BaseDataStoreUserPreference<*, *>> userPreference(): T {
    val dataStore = koinInject<DataStore<Preferences>>()
    return dataStore.userPreference<T>()
}
