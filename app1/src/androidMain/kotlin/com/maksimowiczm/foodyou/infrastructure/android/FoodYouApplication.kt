package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.feature.about.AboutSettings
import com.maksimowiczm.foodyou.feature.calendar.CalendarCard
import com.maksimowiczm.foodyou.feature.diary.DiaryFeature
import com.maksimowiczm.foodyou.feature.language.LanguageFeature
import com.maksimowiczm.foodyou.feature.language.ui.AndroidTrailingContent
import com.maksimowiczm.foodyou.feature.security.SecurityFeature
import com.maksimowiczm.foodyou.feature.system.SystemFeature
import com.maksimowiczm.foodyou.infrastructure.di.initKoin
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module

class FoodYouApplication :
    Application(),
    KoinComponent {
    fun FeatureManager.setupFeatures() {
        addFeature(
            SystemFeature,
            CalendarCard,
            DiaryFeature,
            SecurityFeature,
            LanguageFeature(
                languageSettingsTrailingContent = { modifier ->
                    AndroidTrailingContent(modifier)
                }
            ),
            AboutSettings
        )
    }

    override fun onCreate() {
        super.onCreate()

        val featureManager = FeatureManager()
        with(featureManager) {
            setupFeatures()
        }

        initKoin(
            features = featureManager.features
        ) {
            androidContext(this@FoodYouApplication.applicationContext)
            modules(featureManager.intoModule(this))
        }

        // Block until all features are initialized
        runBlocking {
            featureManager.get<Feature>().forEach { feature ->
                with(feature) {
                    initialize()
                }
            }
        }
    }
}

private fun FeatureManager.intoModule(where: KoinApplication): Module = with(where) {
    module {
        single { this@intoModule }
    }
}
