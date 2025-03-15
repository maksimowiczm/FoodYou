package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.feature.home.calendarcard.CalendarCard
import com.maksimowiczm.foodyou.feature.home.caloriescard.CaloriesCard
import com.maksimowiczm.foodyou.feature.home.mealscard.MealsCard
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.AboutSettings
import com.maksimowiczm.foodyou.feature.settings.goalssettings.GoalsSettings
import com.maksimowiczm.foodyou.feature.settings.language.LanguageSettings
import com.maksimowiczm.foodyou.feature.settings.language.ui.AndroidTrailingContent
import com.maksimowiczm.foodyou.feature.settings.mealssettings.MealsSettings
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.feature.settings.security.SecureScreenSettings
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
        addHomeFeature(
            CalendarCard,
            MealsCard(
                searchHintBuilder = OpenFoodFactsSettings
            ),
            CaloriesCard
        )

        addSettingsFeature(
            OpenFoodFactsSettings,
            MealsSettings,
            GoalsSettings,
            SecureScreenSettings,
            LanguageSettings(
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

        initKoin {
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
