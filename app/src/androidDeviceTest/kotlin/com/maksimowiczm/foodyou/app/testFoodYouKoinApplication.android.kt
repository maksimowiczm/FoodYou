package com.maksimowiczm.foodyou.app

import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.TestScope
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.KoinAppDeclaration

actual val TestScope.platformConfig: KoinAppDeclaration
    get() = { androidContext(InstrumentationRegistry.getInstrumentation().context) }
