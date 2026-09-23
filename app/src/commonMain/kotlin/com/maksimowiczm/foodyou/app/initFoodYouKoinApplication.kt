package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.capabilities.capabilitiesModule
import com.maksimowiczm.foodyou.coreModule
import com.maksimowiczm.foodyou.features.featuresModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

/**
 * Builds a [KoinApplication] instance without starting it globally.
 *
 * This allows for isolated containers in tests. Production code must explicitly call [startKoin]
 * with the returned [KoinApplication] instance to activate it.
 */
fun initFoodYouKoinApplication(appModule: AppModule, config: KoinAppDeclaration? = null) =
    koinApplication {
        modules(
            appModule.module,
            capabilitiesModule,
            coreModule,
            featuresModule,
        )

        config?.invoke(this)
    }
