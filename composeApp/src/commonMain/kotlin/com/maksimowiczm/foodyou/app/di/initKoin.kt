package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.account.di.accountModule
import com.maksimowiczm.foodyou.analytics.di.analyticsModule
import com.maksimowiczm.foodyou.app.infrastructure.dataStoreModule
import com.maksimowiczm.foodyou.app.infrastructure.room.roomModule
import com.maksimowiczm.foodyou.app.ui.appUiModule
import com.maksimowiczm.foodyou.app.ui.common.theme.commonThemeModule
import com.maksimowiczm.foodyou.app.ui.food.foodUiModule
import com.maksimowiczm.foodyou.app.ui.home.homeModule
import com.maksimowiczm.foodyou.app.ui.language.languageModule
import com.maksimowiczm.foodyou.app.ui.onboarding.onboardingModule
import com.maksimowiczm.foodyou.app.ui.personalization.personalizationModule
import com.maksimowiczm.foodyou.app.ui.privacy.privacyModule
import com.maksimowiczm.foodyou.app.ui.profile.profileModule
import com.maksimowiczm.foodyou.app.ui.userproduct.productModule
import com.maksimowiczm.foodyou.common.di.commonModule
import com.maksimowiczm.foodyou.common.event.di.inMemoryEventBusModule
import com.maksimowiczm.foodyou.device.di.deviceModule
import com.maksimowiczm.foodyou.fooddatacentral.di.foodDataCentralModule
import com.maksimowiczm.foodyou.openfoodfacts.di.openFoodFactsModule
import com.maksimowiczm.foodyou.search.di.searchModule
import com.maksimowiczm.foodyou.userproduct.di.userProductModule
import com.maksimowiczm.foodyou.userrecipe.di.userRecipeModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appModule: AppModule, config: KoinAppDeclaration? = null) = startKoin {
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
        openFoodFactsModule,
        userProductModule,
        userRecipeModule,
        searchModule,
    )

    // Ui modules
    modules(
        commonThemeModule,
        onboardingModule,
        appUiModule,
        homeModule,
        languageModule,
        personalizationModule,
        foodUiModule,
        privacyModule,
        profileModule,
        productModule,
    )

    config?.invoke(this)
}
