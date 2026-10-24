package com.maksimowiczm.foodyou.app

import kotlinx.coroutines.test.TestScope
import org.koin.dsl.KoinAppDeclaration

actual val TestScope.platformConfig: KoinAppDeclaration
    get() = {}
