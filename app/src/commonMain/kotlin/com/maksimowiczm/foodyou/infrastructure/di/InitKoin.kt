package com.maksimowiczm.foodyou.infrastructure.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)

    modules(
        coreModule,
        databaseModule,
        dataStoreModule,
        domainModule,
        platformModule
    )

    modules(
        addFoodModule,
        calendarModule,
        goalsModule,
        importExportModule,
        languageModule,
        mealModule,
        measurementModule,
        productModule,
        recipeModule,
        swissFoodCompositionDatabaseModule
    )
}
