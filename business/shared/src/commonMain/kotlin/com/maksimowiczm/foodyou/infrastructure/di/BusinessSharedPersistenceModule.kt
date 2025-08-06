package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.FoodYouDatabase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

// internal const val DATABASE_NAME = "open_source_database.db"
internal const val DATABASE_NAME = "testing.db"

expect val databaseDefinition: Module.() -> KoinDefinition<FoodYouDatabase>

val businessSharedPersistenceModule = module {
    databaseDefinition()
    factory { database.productDao }
    factory { database.recipeDao }
    factory { database.foodSearchDao }
    factory { database.openFoodFactsDao }
    factory { database.usdaPagingKeyDao }
    factory { database.foodEventDao }
}

private val Scope.database: FoodYouDatabase
    get() = get<FoodYouDatabase>()
