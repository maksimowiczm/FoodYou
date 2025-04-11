package com.maksimowiczm.foodyou.infrastructure.di

import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val dataStoreModule = module {
    single {
        createDataStore {
            androidContext().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
        }
    }
}
