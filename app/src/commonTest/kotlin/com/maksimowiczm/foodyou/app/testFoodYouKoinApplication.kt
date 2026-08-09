package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.InMemoryEventBus
import com.maksimowiczm.foodyou.common.event.LoggingEventBus
import kotlinx.coroutines.test.TestScope
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

fun TestScope.testFoodYouKoinApplication(config: KoinAppDeclaration? = null): KoinApplication =
    initFoodYouKoinApplication(
        appModule =
            AppModule(
                foodYouConfig = { single { FoodYouConfig("TEST") } },
                coroutineScope = { backgroundScope },
            )
    ) {
        modules(
            module {
                single { LoggingEventBus(get<InMemoryEventBus>(), get()) }.bind<EventBus>()
            }
        )
        platformConfig.invoke(this)
        config?.invoke(this)
    }

expect val TestScope.platformConfig: KoinAppDeclaration

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
