package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal const val DATABASE_NAME = "open_source_database.db"

internal expect val databaseDefinition: Module.() -> KoinDefinition<FoodYouDatabase>

private val Scope.database
    get() = get<FoodYouDatabase>()

val databaseModule = module {
    databaseDefinition()

    factory { database.productDao }
    factory { database.recipeDao }
    factory { database.measurementDao }
    factory { database.foodSearchDao }
    factory { database.searchLocalDataSource }
    factory { database.mealDao }
}
