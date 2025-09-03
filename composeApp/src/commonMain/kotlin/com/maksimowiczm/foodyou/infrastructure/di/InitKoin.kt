package com.maksimowiczm.foodyou.infrastructure.di

import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializes Koin with the provided configuration and modules.
 *
 * @param applicationCoroutineScope CoroutineScope with whole application lifecycle.
 * @param config Optional KoinAppDeclaration to configure Koin.
 */
fun initKoin(applicationCoroutineScope: CoroutineScope, config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)

        // Shared modules
        modules(appModule, businessSharedModule(applicationCoroutineScope))

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
            FeatureSettingsDatabaseExportCsvProductsModule,
            featureSettingsDatabaseExternalDatabasesModule,
            featureSettingsDatabaseImportCsvProductsModule,
            featureSettingsDatabaseSwissFoodCompositionDatabaseModule,
            featureSettingsLanguageModule,
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
            featureFoodDiaryUpdateModule,
            featureFoodDiaryMealModule,
            featureFoodDiaryQuickAddModule,
        )

        // Onboarding
        modules(featureOnboardingModule)
    }
