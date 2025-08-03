package com.maksimowiczm.foodyou.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore user preference for nullable values.
 */
abstract class NullableDataStoreUserPreference<K, T>(
    dataStore: DataStore<Preferences>,
    key: Preferences.Key<K>
) : BaseDataStoreUserPreference<K, T?>(dataStore, key),
    UserPreference<T?> {

    override fun observe(): Flow<T?> = observeRaw().map { it.toValue() }

    override suspend fun set(value: T?) {
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(key)
            } else {
                val storeValue = value.toStore()
                if (storeValue != null) {
                    preferences[key] = storeValue
                } else {
                    preferences.remove(key)
                }
            }
        }
    }

    /**
     * Maps the stored value to the desired type.
     * @return The mapped value of type T?.
     */
    abstract override fun K?.toValue(): T?
}
