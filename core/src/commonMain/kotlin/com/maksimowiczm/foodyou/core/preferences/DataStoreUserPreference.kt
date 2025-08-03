package com.maksimowiczm.foodyou.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore user preference for non-nullable values.
 */
abstract class DataStoreUserPreference<K, T>(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<K>
) : BaseDataStoreUserPreference<K, T>(dataStore, key),
    UserPreference<T> {

    override fun observe(): Flow<T> = observeRaw().map { it.toValue() }

    override suspend fun set(value: T) {
        dataStore.edit { preferences ->
            val storeValue = value.toStore()
            if (storeValue != null) {
                preferences[key] = storeValue
            } else {
                preferences.remove(key)
            }
        }
    }

    /**
     * Maps the stored value to the desired type.
     * For non-nullable preferences, this should handle null stored values appropriately.
     */
    abstract override fun K?.toValue(): T
}
