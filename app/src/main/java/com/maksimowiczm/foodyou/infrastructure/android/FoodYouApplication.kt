package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.infrastructure.di.coreModule
import com.maksimowiczm.foodyou.infrastructure.di.dataStoreModule
import com.maksimowiczm.foodyou.infrastructure.di.databaseModule
import com.maksimowiczm.foodyou.infrastructure.di.flavourModule
import com.maksimowiczm.foodyou.infrastructure.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

abstract class FoodYouApplication :
    Application(),
    KoinComponent {
    protected abstract fun FeatureManager.setupFeatures()

    final override fun onCreate() {
        super.onCreate()

        val featureManager = FeatureManager()
        with(featureManager) {
            setupFeatures()
        }

        startKoin {
            androidContext(this@FoodYouApplication.applicationContext)

            modules(
                featureManager.intoModule(this),
                platformModule,
                flavourModule,
                databaseModule,
                dataStoreModule,
                coreModule
            )
        }
    }
}

private fun FeatureManager.intoModule(where: KoinApplication): Module = with(where) {
    module {
        single { this@intoModule }
    }
}
