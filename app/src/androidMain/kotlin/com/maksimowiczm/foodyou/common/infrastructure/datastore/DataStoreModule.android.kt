package com.maksimowiczm.foodyou.common.infrastructure.datastore

import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

internal actual fun Scope.produceDataStoreFile(): String =
    androidContext().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
