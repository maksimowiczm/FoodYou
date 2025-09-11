package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.opensource.di.businessOpenSourceModule
import com.maksimowiczm.foodyou.app.infrastructure.di.infrastructureModule
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

        modules(appModule, infrastructureModule(applicationCoroutineScope))

        // Business modules
        modules(businessOpenSourceModule)

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
