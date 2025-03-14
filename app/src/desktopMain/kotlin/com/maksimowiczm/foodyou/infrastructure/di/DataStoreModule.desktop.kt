package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.infrastructure.datastore.createDataStore
import com.maksimowiczm.foodyou.infrastructure.desktop.preferencesDirectory
import java.io.File
import okio.Path.Companion.toPath
import org.koin.dsl.module

actual val dataStoreModule = module {
    single {
        createDataStore {
            File(preferencesDirectory, DATASTORE_FILE_NAME).absolutePath.toPath()
        }
    }
}
