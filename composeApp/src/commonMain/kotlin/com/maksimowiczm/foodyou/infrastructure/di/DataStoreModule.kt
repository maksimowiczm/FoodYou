package com.maksimowiczm.foodyou.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

/**
 * Path to the DataStore file.
 *
 * This is expected to be implemented in the platform-specific code.
 */
expect val dataStorePath: Scope.(fileName: String) -> Path

val dataStoreModule = module {
    single {
        createDataStore { dataStorePath(DATASTORE_FILE_NAME) }
    }
}

fun createDataStore(productFile: () -> Path): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = productFile
    )
