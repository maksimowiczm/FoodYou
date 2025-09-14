package com.maksimowiczm.foodyou.app.infrastructure.opensource.room

import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.binds

internal const val DATABASE_NAME = "open_source_database.db"

internal expect fun Scope.database(): FoodYouDatabase

private val Scope.database: FoodYouDatabase
    get() = get<FoodYouDatabase>()

internal fun Module.roomModule() {
    single<FoodYouDatabase> { database() }.binds(arrayOf(TransactionProvider::class))
    factory { database.productDao }
    factory { database.recipeDao }
    factory { database.foodSearchDao }
    factory { database.openFoodFactsDao }
    factory { database.usdaPagingKeyDao }
    factory { database.foodEventDao }
    factory { database.measurementDao }
    factory { database.mealDao }
    factory { database.sponsorshipDao }
    factory { database.measurementSuggestionDao }
    factory { database.manualDiaryEntryDao }
}
