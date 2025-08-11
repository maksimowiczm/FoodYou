package com.maksimowiczm.foodyou.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.business.shared.domain.network.NetworkConfig
import com.maksimowiczm.foodyou.business.shared.infrastructure.network.FoodYouNetworkConfig
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.FoodYouDatabase
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.FoodYouDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.InitializeMealsCallback
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

// internal const val DATABASE_NAME = "open_source_database.db"
internal const val DATABASE_NAME = "testing.db"

expect fun Scope.createDatabaseBuilder(): RoomDatabase.Builder<FoodYouDatabase>

private val Scope.database: FoodYouDatabase
    get() = get<FoodYouDatabase>()

private val databaseDefinition: Module.() -> Unit = {
    single { createDatabaseBuilder().buildDatabase(InitializeMealsCallback(get())) }
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

expect fun Scope.createDataStore(): DataStore<Preferences>

private val dataStoreDefinition: Module.() -> KoinDefinition<DataStore<Preferences>> = {
    single { createDataStore() }
}

val businessSharedPersistenceModule = module {
    databaseDefinition()
    dataStoreDefinition()

    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
}
