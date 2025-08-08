package com.maksimowiczm.foodyou.infrastructure.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    // Shared modules
    modules(
        appModule,
        businessSharedPersistenceModule,
        featureSharedModule,
        sharedCommonModule
    )

    // Business modules
    modules(
        businessFoodModule,
        businessFoodDiaryModule,
        businessSettingsModule,
        businessSponsorshipModule,
    )

    // About
    modules(
        featureAboutMasterModule,
        featureAboutSponsorModule,
    )

    // Settings
    modules(
        featureSettingsGoalsModule,
        featureSettingsLanguageModule,
        featureSettingsMealModule,
        featureSettingsPersonalizationModule
    )

    // Home
    modules(
        featureHomeModule
    )

    // Goals
    modules(
        featureGoalsModule
    )

    modules(
//        foodModule,
//        importExportModule,
//        onboardingModule,
        openFoodFactsModule,
//        swissFoodCompositionDatabaseModule,
        usdaModule
    )
}
