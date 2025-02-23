package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.feature.about.AboutFeature
import com.maksimowiczm.foodyou.feature.addfood.OpenSourceAddFoodFeature
import com.maksimowiczm.foodyou.feature.calendar.CalendarFeature
import com.maksimowiczm.foodyou.feature.diary.OpenSourceDiaryFeature
import com.maksimowiczm.foodyou.feature.openfoodfacts.OpenFoodFactsFeature
import com.maksimowiczm.foodyou.feature.system.SystemFeature
import com.maksimowiczm.foodyou.infrastructure.di.dataStoreModule
import com.maksimowiczm.foodyou.infrastructure.di.flavourModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FoodYouApplication : Application() {
    private fun setupFeatures() {
        FeatureManager.add(
            SystemFeature,
            CalendarFeature,
            OpenFoodFactsFeature,
            OpenSourceDiaryFeature,
            OpenSourceAddFoodFeature,
            AboutFeature
        )
    }

    override fun onCreate() {
        super.onCreate()

        setupFeatures()

        startKoin {
            androidContext(this@FoodYouApplication.applicationContext)

            modules(
                flavourModule,
                dataStoreModule
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
