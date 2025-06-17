package com.maksimowiczm.foodyou.core.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Factory for creating DataStore user preference instances.
 */
class DataStoreUserPreferenceFactory(val dataStore: DataStore<Preferences>) {

    inline fun <reified T : BaseDataStoreUserPreference<*, *>> create(): T {
        val clazz = T::class
        val constructor = clazz.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters.first().type.classifier == DataStore::class
        } ?: error("No suitable constructor found for ${clazz.simpleName}")

        return constructor.call(dataStore)
    }
}
