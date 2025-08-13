package com.maksimowiczm.foodyou.infrastructure.di

import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin with the provided configuration and modules.
 *
 * @param coroutineScope CoroutineScope with whole application lifecycle.
 * @param config Optional KoinAppDeclaration to configure Koin.
 */
fun initKoin(coroutineScope: CoroutineScope, config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    // Shared modules
    modules(
        appModule,
        businessSharedPersistenceModule,
        featureSharedModule,
        sharedCommonModule(coroutineScope),
    )

    // Business modules
    modules(
        businessFoodModule,
        businessFoodDiaryModule,
        businessSettingsModule,
        businessSponsorshipModule,
    )

    // About
    modules(featureAboutMasterModule, featureAboutSponsorModule)

    // Settings
    modules(
        featureSettingsDatabaseDatabaseDumpModule,
        featureSettingsDatabaseExternalDatabasesModule,
        featureSettingsGoalsModule,
        featureSettingsLanguageModule,
        featureSettingsMealModule,
        featureSettingsPersonalizationModule,
    )

    // Home
    modules(featureHomeModule)

    // Goals
    modules(featureGoalsModule)

    // Food
    modules(featureFoodProductModule, featureFoodRecipeModule, featureFoodSharedModule)

    // Food Diary
    modules(
        featureFoodDiaryAddModule,
        featureFoodDiarySearchModule,
        featureFoodDiarySharedModule,
        featureFoodDiaryUpdateModule,
    )

    modules(openFoodFactsModule, usdaModule)
}
