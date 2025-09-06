package com.maksimowiczm.foodyou.app.infrastructure.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.app.infrastructure.DateProviderImpl
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouNetworkConfig
import com.maksimowiczm.foodyou.app.infrastructure.SharedFlowEventBus
import com.maksimowiczm.foodyou.app.infrastructure.SponsorRepositoryImpl
import com.maksimowiczm.foodyou.app.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.app.infrastructure.TranslationRepositoryImpl
import com.maksimowiczm.foodyou.app.infrastructure.VibeCsvParser
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreGoalsRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreSettingsRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreSponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreUserIdentifierProvider
import com.maksimowiczm.foodyou.app.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomFoodDiaryEntryRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomManualDiaryEntryRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomMealRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.InitializeMealsCallback
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.domain.TranslationRepository
import com.maksimowiczm.foodyou.business.shared.application.csv.CsvParser
import com.maksimowiczm.foodyou.business.shared.application.database.DatabaseDumpService
import com.maksimowiczm.foodyou.business.shared.domain.config.NetworkConfig
import com.maksimowiczm.foodyou.business.shared.domain.identity.UserIdentifierProvider
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.goals.domain.repository.GoalsRepository
import com.maksimowiczm.foodyou.infrastructure.di.applicationCoroutineScopeQualifier
import com.maksimowiczm.foodyou.shared.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.date.DateProvider
import com.maksimowiczm.foodyou.shared.event.EventBus
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import com.maksimowiczm.foodyou.sponsorship.domain.repository.SponsorRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.dsl.onClose

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
    factory { database.manualDiaryEntryDao }
}

internal const val DATASTORE_FILE_NAME = "user_preferences.preferences_pb"

internal expect fun Scope.createDataStore(): DataStore<Preferences>

private val dataStoreDefinition: Module.() -> KoinDefinition<DataStore<Preferences>> = {
    single { createDataStore() }
}

internal expect val systemDetails: Module.() -> KoinDefinition<out SystemDetails>

fun infrastructureModule(applicationCoroutineScope: CoroutineScope) = module {
    single(applicationCoroutineScopeQualifier) { applicationCoroutineScope }

    databaseDefinition()
    dataStoreDefinition()

    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
    factoryOf(::VibeCsvParser).bind<CsvParser>()

    singleOf(::SharedFlowEventBus).bind<EventBus>()

    singleOf(::DateProviderImpl).bind<DateProvider>()

    factoryOf(::DataStoreUserIdentifierProvider).bind<UserIdentifierProvider>()

    // ---
    single(named("ktorSponsorshipHttpClient")) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }

    factory {
        FoodYouSponsorsApiClient(client = get(named("ktorSponsorshipHttpClient")), config = get())
    }

    factoryOf(::DataStoreSponsorshipPreferencesDataSource) {
            qualifier = named(SponsorshipPreferences::class.qualifiedName!!)
        }
        .bind<UserPreferencesRepository<SponsorshipPreferences>>()

    factory {
            SponsorRepositoryImpl(
                sponsorshipDao = get(),
                networkDataSource = get(),
                preferences = get(named(SponsorshipPreferences::class.qualifiedName!!)),
                logger = get(),
            )
        }
        .bind<SponsorRepository>()

    // ---
    factory {
            TranslationRepositoryImpl(
                systemDetails = get(),
                settingsRepository = get(named(Settings::class.qualifiedName!!)),
            )
        }
        .bind<TranslationRepository>()
    factoryOf(::DataStoreSettingsRepository).bind<UserPreferencesRepository<Settings>>()
    systemDetails()

    // ---

    factoryOf(::RoomFoodDiaryEntryRepository).bind<FoodDiaryEntryRepository>()
    factoryOf(::RoomManualDiaryEntryRepository).bind<ManualDiaryEntryRepository>()
    factoryOf(::RoomMealRepository).bind<MealRepository>()
    factoryOf(::DataStoreMealsPreferencesRepository) {
            qualifier = named(MealsPreferences::class.qualifiedName!!)
        }
        .bind<UserPreferencesRepository<MealsPreferences>>()
    factoryOf(::DataStoreGoalsRepository).bind<GoalsRepository>()
}
