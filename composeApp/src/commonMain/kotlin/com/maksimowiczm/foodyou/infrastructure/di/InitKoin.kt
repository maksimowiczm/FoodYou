package com.maksimowiczm.foodyou.infrastructure.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        dataStoreModule
    )

    modules(
        sharedCommonModule,
        businessFoodModule,
        businessFoodDiaryModule,
        businessSharedPersistenceModule,
        businessSponsorshipModule,
        localDatabaseModule
    )

    modules(
//        aboutModule,
//        foodModule,
//        foodDiaryModule,
//        importExportModule,
//        languageModule,
//        onboardingModule,
        openFoodFactsModule,
//        swissFoodCompositionDatabaseModule,
        usdaModule
    )
}
