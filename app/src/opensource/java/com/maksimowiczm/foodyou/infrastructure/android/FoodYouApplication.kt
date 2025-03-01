package com.maksimowiczm.foodyou.infrastructure.android

import android.app.Application
import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.feature.home.calendarcard.CalendarCard
import com.maksimowiczm.foodyou.feature.home.caloriescard.CaloriesCard
import com.maksimowiczm.foodyou.feature.home.mealscard.MealsCard
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.zxingCameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.AboutSettings
import com.maksimowiczm.foodyou.feature.settings.goalssettings.GoalsSettings
import com.maksimowiczm.foodyou.feature.settings.language.LanguageSettings
import com.maksimowiczm.foodyou.feature.settings.language.ui.AndroidTrailingContent
import com.maksimowiczm.foodyou.feature.settings.mealssettings.MealsSettings
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.infrastructure.di.dataModule
import com.maksimowiczm.foodyou.infrastructure.di.dataStoreModule
import com.maksimowiczm.foodyou.infrastructure.di.databaseModule
import com.maksimowiczm.foodyou.infrastructure.di.featureModule
import com.maksimowiczm.foodyou.infrastructure.di.flavourModule
import com.maksimowiczm.foodyou.infrastructure.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FoodYouApplication : Application() {
    private fun setupFeatures() {
        FeatureManager.addHomeFeature(
            CalendarCard,
            MealsCard(
                searchHintBuilder = OpenFoodFactsSettings,
                barcodeScannerScreen = zxingCameraBarcodeScannerScreen
            ),
            CaloriesCard
        )

        FeatureManager.addSettingsFeature(
            OpenFoodFactsSettings,
            MealsSettings,
            GoalsSettings,
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

        setupFeatures()

        startKoin {
            androidContext(this@FoodYouApplication.applicationContext)

            modules(
                platformModule,
                flavourModule,
                databaseModule,
                dataStoreModule,
                dataModule,
                featureModule
            )
        }
    }
}
