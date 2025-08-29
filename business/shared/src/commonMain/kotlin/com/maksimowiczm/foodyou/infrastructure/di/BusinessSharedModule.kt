package com.maksimowiczm.foodyou.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.business.shared.application.csv.CsvParser
import com.maksimowiczm.foodyou.business.shared.application.database.DatabaseDumpService
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.domain.config.NetworkConfig
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.infrastructure.DateProviderImpl
import com.maksimowiczm.foodyou.business.shared.infrastructure.FoodYouNetworkConfig
import com.maksimowiczm.foodyou.business.shared.infrastructure.SharedFlowEventBus
import com.maksimowiczm.foodyou.business.shared.infrastructure.VibeCsvParser
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.fooddiary.InitializeMealsCallback
import kotlinx.coroutines.CoroutineScope
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

internal const val DATABASE_NAME = "open_source_database.db"

internal expect fun Scope.database(): FoodYouDatabase

private val Scope.database: FoodYouDatabase
    get() = get<FoodYouDatabase>()

private val databaseDefinition: Module.() -> Unit = {
    factory { InitializeMealsCallback(get()) }
    single<FoodYouDatabase> { database() }
        .binds(arrayOf(TransactionProvider::class, DatabaseDumpService::class))
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
}

internal const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

internal expect fun Scope.createDataStore(): DataStore<Preferences>

private val dataStoreDefinition: Module.() -> KoinDefinition<DataStore<Preferences>> = {
    single { createDataStore() }
}

private const val APPLICATION_COROUTINE_SCOPE = "APPLICATION_COROUTINE_SCOPE"

fun Scope.applicationCoroutineScope(): CoroutineScope = get(named(APPLICATION_COROUTINE_SCOPE))

fun businessSharedModule(applicationCoroutineScope: CoroutineScope) = module {
    single(named(APPLICATION_COROUTINE_SCOPE)) { applicationCoroutineScope }

    databaseDefinition()
    dataStoreDefinition()

    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
    factoryOf(::VibeCsvParser).bind<CsvParser>()

    singleOf(::SharedFlowEventBus).bind<EventBus>()

    singleOf(::DateProviderImpl).bind<DateProvider>()
}
