package com.maksimowiczm.foodyou.preferences.infrastructure

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.maksimowiczm.foodyou.preferences.domain.UserPreferences
import kotlinx.coroutines.flow.map

abstract class DataStoreProvider<T : UserPreferences>(
    private val dataStore: DataStore<Preferences>
) : UserPreferenceProvider<T> {
    protected abstract fun Preferences.map(): T

    protected abstract fun MutablePreferences.apply(value: T)

    final override fun observe() = dataStore.data.map { it.map() }

    final override suspend fun update(transform: (T) -> T) {
        dataStore.edit { it.apply(transform(it.map())) }
    }
}
