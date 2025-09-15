package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.app.business.opensource.di.businessOpenSourceModule
import com.maksimowiczm.foodyou.app.business.shared.di.businessSharedModule
import com.maksimowiczm.foodyou.app.infrastructure.opensource.infrastructureOpenSourceModule
import com.maksimowiczm.foodyou.app.infrastructure.shared.infrastructureSharedModule
import com.maksimowiczm.foodyou.app.ui.changelog.uiChangelogModule
import com.maksimowiczm.foodyou.app.ui.language.uiLanguageModule
import com.maksimowiczm.foodyou.app.ui.personalization.uiPersonalizationModule
import com.maksimowiczm.foodyou.app.ui.sponsor.uiSponsorModule
import com.maksimowiczm.foodyou.app.ui.theme.uiThemeModule
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

        modules(appModule)

        // Infrastructure modules
        modules(
            infrastructureOpenSourceModule,
            infrastructureSharedModule(applicationCoroutineScope),
        )

        // Business modules
        modules(businessOpenSourceModule, businessSharedModule)

        // Settings
        modules(
            featureSettingsDatabaseDatabaseDumpModule,
            FeatureSettingsDatabaseExportCsvProductsModule,
            featureSettingsDatabaseExternalDatabasesModule,
            featureSettingsDatabaseImportCsvProductsModule,
            featureSettingsDatabaseSwissFoodCompositionDatabaseModule,
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

        // Theme
        modules(uiThemeModule)

        modules(uiSponsorModule)

        modules(uiChangelogModule)

        modules(uiLanguageModule)

        modules(uiPersonalizationModule)
    }
