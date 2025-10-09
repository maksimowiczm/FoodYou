package com.maksimowiczm.foodyou.app.infrastructure

import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.produceDataStoreFile(): String {
    return androidContext().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
}
