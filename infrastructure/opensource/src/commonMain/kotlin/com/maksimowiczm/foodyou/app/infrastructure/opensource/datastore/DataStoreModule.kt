package com.maksimowiczm.foodyou.app.infrastructure.opensource.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module
import org.koin.core.scope.Scope

internal const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

internal expect fun Scope.createDataStore(): DataStore<Preferences>

internal fun Module.dataStoreModule() {
    single { createDataStore() }
}
