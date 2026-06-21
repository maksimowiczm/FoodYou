package com.maksimowiczm.foodyou.app

import androidx.test.platform.app.InstrumentationRegistry
import com.maksimowiczm.foodyou.app.di.AppModule
import com.maksimowiczm.foodyou.app.di.initFoodYouKoinApplication
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.event.EventBus
import com.maksimowiczm.foodyou.common.event.InMemoryEventBus
import com.maksimowiczm.foodyou.common.event.LoggingEventBus
import kotlinx.coroutines.test.TestScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun TestScope.testFoodYouKoinApplication(config: KoinAppDeclaration?): KoinApplication =
    initFoodYouKoinApplication(
        appModule = AppModule(foodYouConfig = { single { FoodYouConfig("TEST") } })
    ) {
        androidContext(InstrumentationRegistry.getInstrumentation().context)
        modules(
            module {
                applicationCoroutineScope { backgroundScope }
                single { LoggingEventBus(get<InMemoryEventBus>(), get()) }.bind<EventBus>()
            }
        )
        config?.invoke(this)
    }
