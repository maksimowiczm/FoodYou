package com.maksimowiczm.foodyou.core.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.FeatureManager
import com.maksimowiczm.foodyou.core.infrastructure.di.dataStoreModule
import com.maksimowiczm.foodyou.core.infrastructure.di.databaseModule
import com.maksimowiczm.foodyou.core.infrastructure.di.flavourModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

abstract class FoodYouApplication : Application() {
    abstract fun setupFeatures()

    override fun onCreate() {
        super.onCreate()

        setupFeatures()

        startKoin {
            androidContext(this@FoodYouApplication.applicationContext)

            modules(
                dataStoreModule,
                databaseModule,
                flavourModule
            )

            FeatureManager.features
                .filterIsInstance<Feature.Koin>()
                .forEach {
                    with(it) {
                        setup()
                    }
                }
        }
    }
}
