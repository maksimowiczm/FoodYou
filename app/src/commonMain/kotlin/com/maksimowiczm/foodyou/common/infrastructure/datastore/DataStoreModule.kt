package com.maksimowiczm.foodyou.common.infrastructure.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.core.scope.Scope

internal const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

internal expect fun Scope.produceDataStoreFile(): String

fun Module.dataStoreModule() {
    single { PreferenceDataStoreFactory.createWithPath { produceDataStoreFile().toPath() } }
}
