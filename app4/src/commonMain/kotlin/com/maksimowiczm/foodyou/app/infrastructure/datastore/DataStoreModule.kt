package com.maksimowiczm.foodyou.app.infrastructure.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

internal expect fun Scope.produceDataStoreFile(): String

val dataStoreModule = module {
    single { PreferenceDataStoreFactory.createWithPath { produceDataStoreFile().toPath() } }
}
