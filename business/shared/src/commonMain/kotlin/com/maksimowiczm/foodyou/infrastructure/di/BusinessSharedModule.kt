package com.maksimowiczm.foodyou.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.csv.CsvParser
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseDumpService
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.system.SystemDetails
import com.maksimowiczm.foodyou.business.shared.application.query.QueryBus
import com.maksimowiczm.foodyou.business.shared.infrastructure.command.KoinCommandBus
import com.maksimowiczm.foodyou.business.shared.infrastructure.csv.VibeCsvParser
import com.maksimowiczm.foodyou.business.shared.infrastructure.date.DateProviderImpl
import com.maksimowiczm.foodyou.business.shared.infrastructure.event.SharedFlowEventBus
import com.maksimowiczm.foodyou.business.shared.infrastructure.network.FoodYouNetworkConfig
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.FoodYouDatabase
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.InitializeMealsCallback
import com.maksimowiczm.foodyou.business.shared.infrastructure.query.KoinQueryBus
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
        .binds(arrayOf(DatabaseTransactionProvider::class, DatabaseDumpService::class))
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

expect val systemDetails: Module.() -> KoinDefinition<out SystemDetails>

private const val APPLICATION_COROUTINE_SCOPE = "APPLICATION_COROUTINE_SCOPE"

fun Scope.applicationCoroutineScope(): CoroutineScope = get(named(APPLICATION_COROUTINE_SCOPE))

fun businessSharedModule(applicationCoroutineScope: CoroutineScope) = module {
    single(named(APPLICATION_COROUTINE_SCOPE)) { applicationCoroutineScope }

    databaseDefinition()
    dataStoreDefinition()
    systemDetails()

    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
    factoryOf(::VibeCsvParser).bind<CsvParser>()

    singleOf(::KoinCommandBus).bind<CommandBus>()
    singleOf(::KoinQueryBus).bind<QueryBus>()
    singleOf(::SharedFlowEventBus).bind<EventBus>()

    singleOf(::DateProviderImpl).bind<DateProvider>()
}
