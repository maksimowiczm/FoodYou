package com.maksimowiczm.foodyou.infrastructure.di

import okio.Path
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual val dataStorePath: Scope.(String) -> Path = { fileName ->
    androidContext().filesDir.resolve(fileName).absolutePath.toPath()
}
