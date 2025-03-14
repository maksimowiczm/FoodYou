package com.maksimowiczm.foodyou.infrastructure.android

import com.maksimowiczm.foodyou.feature.FeatureManager
import com.maksimowiczm.foodyou.feature.home.calendarcard.CalendarCard
import com.maksimowiczm.foodyou.feature.home.caloriescard.CaloriesCard
import com.maksimowiczm.foodyou.feature.home.mealscard.MealsCard
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.app.barcodescanner.zxingCameraBarcodeScannerScreen
import com.maksimowiczm.foodyou.feature.settings.aboutsettings.AboutSettings
import com.maksimowiczm.foodyou.feature.settings.goalssettings.GoalsSettings
import com.maksimowiczm.foodyou.feature.settings.laguage.AndroidTrailingContent
import com.maksimowiczm.foodyou.feature.settings.language.LanguageSettings
import com.maksimowiczm.foodyou.feature.settings.mealssettings.MealsSettings
import com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.OpenFoodFactsSettings
import com.maksimowiczm.foodyou.feature.settings.security.SecureScreenSettings

class OpenSourceApplication : FoodYouApplication() {
    override fun FeatureManager.setupFeatures() {
        addHomeFeature(
            CalendarCard,
            MealsCard(
                searchHintBuilder = OpenFoodFactsSettings,
                barcodeScannerScreen = zxingCameraBarcodeScannerScreen
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
}
