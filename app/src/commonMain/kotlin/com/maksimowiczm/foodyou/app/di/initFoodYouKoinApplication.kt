package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.account.di.accountModule
import com.maksimowiczm.foodyou.analytics.di.analyticsModule
import com.maksimowiczm.foodyou.app.infrastructure.dataStoreModule
import com.maksimowiczm.foodyou.app.infrastructure.room.roomModule
import com.maksimowiczm.foodyou.app.ui.appUiModule
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.foodBrowsingModule
import com.maksimowiczm.foodyou.capabilities.fooddetails.foodDetailsModule
import com.maksimowiczm.foodyou.common.di.commonModule
import com.maksimowiczm.foodyou.common.event.di.inMemoryEventBusModule
import com.maksimowiczm.foodyou.device.di.deviceModule
import com.maksimowiczm.foodyou.features.diary.diaryUiModule
import com.maksimowiczm.foodyou.features.fooddatacentral.foodDataCentralUiModule
import com.maksimowiczm.foodyou.features.home.homeModule
import com.maksimowiczm.foodyou.features.language.languageModule
import com.maksimowiczm.foodyou.features.meal.mealModule
import com.maksimowiczm.foodyou.features.onboarding.onboardingModule
import com.maksimowiczm.foodyou.features.openfoodfacts.openFoodFactsModule as openFoodFactsUiModule
import com.maksimowiczm.foodyou.features.personalization.personalizationModule
import com.maksimowiczm.foodyou.features.privacy.privacyModule
import com.maksimowiczm.foodyou.features.profile.profileModule
import com.maksimowiczm.foodyou.features.userproduct.productModule
import com.maksimowiczm.foodyou.features.userproduct.userProductModule
import com.maksimowiczm.foodyou.features.userrecipe.userRecipeModule
import com.maksimowiczm.foodyou.fooddatacentral.di.foodDataCentralModule
import com.maksimowiczm.foodyou.fooddiary.di.foodDiaryModule
import com.maksimowiczm.foodyou.mealplan.di.mealPlanModule
import com.maksimowiczm.foodyou.openfoodfacts.di.openFoodFactsModule
import com.maksimowiczm.foodyou.search.di.searchModule
import com.maksimowiczm.foodyou.shared.ui.theme.commonThemeModule
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
        // App modules
        modules(dataStoreModule, roomModule, appModule.module)

        // Common modules
        modules(inMemoryEventBusModule, commonModule)

        // Feature modules
        modules(
            accountModule,
            analyticsModule,
            deviceModule,
            foodDataCentralModule,
            foodDiaryModule,
            mealPlanModule,
            openFoodFactsModule,
            com.maksimowiczm.foodyou.userproduct.di.userProductModule,
            com.maksimowiczm.foodyou.userrecipe.di.userRecipeModule,
            searchModule,
        )

        // Capabilities
        modules(foodBrowsingModule, foodDetailsModule)

        // Ui modules
        modules(
            commonThemeModule,
            diaryUiModule,
            onboardingModule,
            appUiModule,
            homeModule,
            languageModule,
            mealModule,
            personalizationModule,
            userProductModule,
            userRecipeModule,
            foodDataCentralUiModule,
            openFoodFactsUiModule,
            privacyModule,
            profileModule,
            productModule,
        )

        config?.invoke(this)
    }
