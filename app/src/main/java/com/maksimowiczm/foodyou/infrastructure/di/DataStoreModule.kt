package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.infrastructure.datastore.createDataStore
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single {
        createDataStore {
            androidContext().filesDir.resolve("user_preferences.preferences_pb").absolutePath.toPath()
        }
    }
}
