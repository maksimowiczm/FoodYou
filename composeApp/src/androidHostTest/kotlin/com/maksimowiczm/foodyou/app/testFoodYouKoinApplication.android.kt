package com.maksimowiczm.foodyou.app

import kotlinx.coroutines.test.TestScope
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration

actual fun TestScope.testFoodYouKoinApplication(config: KoinAppDeclaration?): KoinApplication =
    unavailable("Skipping integration tests on this platform")
