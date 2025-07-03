package com.maksimowiczm.foodyou.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Base abstract class for DataStore user preferences with common functionality.
 */
abstract class BaseDataStoreUserPreference<S, T>(
    protected val dataStore: DataStore<Preferences>,
    protected val key: Preferences.Key<S>
) {
    /**
     * Maps the stored value to the desired type.
     * @return The mapped value of type T.
     */
    abstract fun S?.toValue(): T

    /**
     * Converts the value of type T to the stored type S.
     * @return The value to be stored in the DataStore.
     */
    abstract fun T.toStore(): S?

    protected fun observeRaw(): Flow<S?> = dataStore.data.map { preferences ->
        preferences[key]
    }
}

// Alias it to fit it in formatter line length
private typealias DS = DataStore<Preferences>
inline fun <reified T : BaseDataStoreUserPreference<*, *>> DS.userPreference(): T =
    DataStoreUserPreferenceFactory(this).create<T>()
