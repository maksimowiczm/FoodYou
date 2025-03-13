package com.maksimowiczm.foodyou.infrastructure.android

import com.maksimowiczm.foodyou.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.data.AndroidOpenSourceLinkHandler
import com.maksimowiczm.foodyou.data.AndroidStringFormatRepository
import com.maksimowiczm.foodyou.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.data.DateProviderImpl
import com.maksimowiczm.foodyou.data.DiaryRepositoryImpl
import com.maksimowiczm.foodyou.data.OpenFoodFactsSettingsRepositoryImpl
import com.maksimowiczm.foodyou.data.ProductRepositoryImpl
import com.maksimowiczm.foodyou.data.linkHandler
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
import com.maksimowiczm.foodyou.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.network.ProductRemoteMediatorFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

class OpenSourceApplication : FoodYouApplication() {
    private val calendarCard = CalendarCard(
        stringFormatRepository = { factoryOf(::AndroidStringFormatRepository) },
        dateProvider = { factory { DateProviderImpl() } }
    )

    private val openFoodFactsSettings = OpenFoodFactsSettings(
        openFoodFactsSettingsRepository = { factoryOf(::OpenFoodFactsSettingsRepositoryImpl) },
        systemInfoRepository = { factoryOf(::AndroidSystemInfoRepository) }
    )

    private val mealsCard = MealsCard(
        searchHintBuilder = openFoodFactsSettings,
        barcodeScannerScreen = zxingCameraBarcodeScannerScreen,
        addFoodRepository = {
            singleOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

            factory {
                AddFoodRepositoryImpl(
                    addFoodDao = get(),
                    productDao = get(),
                    productRemoteMediatorFactory = get()
                )
            }
        },
        productRepository = { factoryOf(::ProductRepositoryImpl) },
        diaryRepository = { factoryOf(::DiaryRepositoryImpl) },
        stringFormatRepository = { factoryOf(::AndroidStringFormatRepository) },
        dateProvider = { factory { DateProviderImpl() } }
    )

    private val caloriesCard = CaloriesCard(
        diaryRepository = { factoryOf(::DiaryRepositoryImpl) }
    )

    private val mealsSettings = MealsSettings(
        diaryRepository = { factoryOf(::DiaryRepositoryImpl) },
        stringFormatRepository = { factoryOf(::AndroidStringFormatRepository) }
    )

    private val goalsSettings = GoalsSettings(
        diaryRepository = { factoryOf(::DiaryRepositoryImpl) }
    )

    private val languageSettings = LanguageSettings(
        languageSettingsTrailingContent = { modifier -> AndroidTrailingContent(modifier) },
        androidSystemInfoRepository = { factoryOf(::AndroidSystemInfoRepository) },
        linkHandler = { factory { androidContext().linkHandler } }
    )

    private val aboutSettings = AboutSettings(
        openSourceLinkHandler = { factoryOf(::AndroidOpenSourceLinkHandler) }
    )

    override fun FeatureManager.setupFeatures() {
        addHomeFeature(
            calendarCard,
            mealsCard,
            caloriesCard
        )

        addSettingsFeature(
            openFoodFactsSettings,
            mealsSettings,
            goalsSettings,
            languageSettings,
            aboutSettings
        )
    }
}
