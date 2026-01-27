package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.account.di.accountModule
import com.maksimowiczm.foodyou.analytics.di.analyticsModule
import com.maksimowiczm.foodyou.app.infrastructure.config.configModule
import com.maksimowiczm.foodyou.app.infrastructure.datastore.dataStoreModule
import com.maksimowiczm.foodyou.app.infrastructure.room.roomModule
import com.maksimowiczm.foodyou.app.ui.appUiModule
import com.maksimowiczm.foodyou.app.ui.common.theme.commonThemeModule
import com.maksimowiczm.foodyou.app.ui.food.details.foodDetailsModule
import com.maksimowiczm.foodyou.app.ui.home.homeModule
import com.maksimowiczm.foodyou.app.ui.language.languageModule
import com.maksimowiczm.foodyou.app.ui.onboarding.onboardingModule
import com.maksimowiczm.foodyou.app.ui.personalization.personalizationModule
import com.maksimowiczm.foodyou.app.ui.privacy.privacyModule
import com.maksimowiczm.foodyou.app.ui.profile.profileModule
import com.maksimowiczm.foodyou.app.ui.userfood.productModule
import com.maksimowiczm.foodyou.common.clock.di.clockModule
import com.maksimowiczm.foodyou.common.di.commonModule
import com.maksimowiczm.foodyou.common.event.di.inMemoryEventBusModule
import com.maksimowiczm.foodyou.common.logger.di.loggerModule
import com.maksimowiczm.foodyou.device.di.deviceModule
import com.maksimowiczm.foodyou.food.di.foodModule
import com.maksimowiczm.foodyou.food.search.di.foodSearchModule
import com.maksimowiczm.foodyou.openfoodfacts.di.openFoodFactsModule
import com.maksimowiczm.foodyou.userfood.di.userFoodModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication = startKoin {
    // App modules
    modules(dataStoreModule, roomModule, configModule)

    // Common modules
    modules(clockModule, inMemoryEventBusModule, commonModule, loggerModule)

    // Feature modules
    modules(
        accountModule,
        analyticsModule,
        deviceModule,
        foodModule,
        foodSearchModule,
        openFoodFactsModule,
        userFoodModule,
    )

    // Ui modules
    modules(
        commonThemeModule,
        onboardingModule,
        appUiModule,
        homeModule,
        languageModule,
        personalizationModule,
        com.maksimowiczm.foodyou.app.ui.food.search.foodSearchModule,
        foodDetailsModule,
        privacyModule,
        profileModule,
        productModule,
    )

    config?.invoke(this)
}
