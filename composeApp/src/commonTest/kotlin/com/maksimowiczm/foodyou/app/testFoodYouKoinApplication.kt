package com.maksimowiczm.foodyou.app

import kotlinx.coroutines.test.TestScope
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect fun TestScope.testFoodYouKoinApplication(config: KoinAppDeclaration? = null): KoinApplication

suspend inline fun TestScope.runKoin(vararg modules: Module, block: suspend Koin.() -> Unit) {
    val koin = testFoodYouKoinApplication {
        allowOverride(true)
        modules(*modules)
    }
    try {
        block(koin.koin)
    } finally {
        koin.close()
    }
}
