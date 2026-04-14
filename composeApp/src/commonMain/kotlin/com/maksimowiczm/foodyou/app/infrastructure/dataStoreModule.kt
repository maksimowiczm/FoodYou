package com.maksimowiczm.foodyou.app.infrastructure

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.resolve
import okio.Path.Companion.toPath
import org.koin.dsl.module

private const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

val dataStoreModule = module {
    single {
        PreferenceDataStoreFactory.createWithPath {
            FileKit.databasesDir.resolve(DATASTORE_FILE_NAME).path.toPath()
        }
    }
}
