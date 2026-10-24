package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.unavailable
import kotlinx.coroutines.test.TestScope
import org.koin.dsl.KoinAppDeclaration

actual val TestScope.platformConfig: KoinAppDeclaration
    get() = unavailable("Koin tests unavailable on Android host platform")
