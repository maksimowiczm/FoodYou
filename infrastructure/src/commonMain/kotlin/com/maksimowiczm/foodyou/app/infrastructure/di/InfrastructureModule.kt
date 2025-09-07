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
import com.maksimowiczm.foodyou.app.infrastructure.compose.ComposeMealsProvider
import com.maksimowiczm.foodyou.app.infrastructure.compose.ComposeSwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreFoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreGoalsRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreSettingsRepository
import com.maksimowiczm.foodyou.app.infrastructure.datastore.DataStoreSponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.app.infrastructure.foodyousponsors.FoodYouSponsorsApiClient
import com.maksimowiczm.foodyou.app.infrastructure.network.FoodRemoteMediatorFactoryAggregateImpl
import com.maksimowiczm.foodyou.app.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.network.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.app.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.USDAFacade
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.USDARemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomFoodDiaryEntryRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomFoodHistoryRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomFoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomFoodSearchHistoryRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomFoodSearchRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomManualDiaryEntryRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomMealRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomProductRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomRecipeRepository
import com.maksimowiczm.foodyou.app.infrastructure.room.RoomUsdaPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.InitializeMealsCallback
import com.maksimowiczm.foodyou.app.infrastructure.room.fooddiary.MealsProvider
import com.maksimowiczm.foodyou.business.food.domain.FoodRemoteMediatorFactoryAggregate
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.business.settings.domain.TranslationRepository
import com.maksimowiczm.foodyou.business.shared.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.business.shared.di.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.business.shared.domain.config.NetworkConfig
import com.maksimowiczm.foodyou.business.shared.domain.csv.CsvParser
import com.maksimowiczm.foodyou.business.shared.domain.database.DatabaseDumpService
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.food.domain.repository.RecipeRepository
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.goals.domain.repository.GoalsRepository
import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.event.EventBus
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
    factoryOf(::ComposeMealsProvider).bind<MealsProvider>()
    factoryOf(::InitializeMealsCallback)
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
    applicationCoroutineScope { applicationCoroutineScope }

    databaseDefinition()
    dataStoreDefinition()

    factoryOf(::FoodYouNetworkConfig).bind<NetworkConfig>()
    factoryOf(::VibeCsvParser).bind<CsvParser>()

    singleOf(::SharedFlowEventBus).bind<EventBus>()

    singleOf(::DateProviderImpl).bind<DateProvider>()

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

    userPreferencesRepositoryOf(::DataStoreSponsorshipPreferencesDataSource)

    factory {
            SponsorRepositoryImpl(
                sponsorshipDao = get(),
                networkDataSource = get(),
                preferences = userPreferencesRepository(),
                logger = get(),
            )
        }
        .bind<SponsorRepository>()

    // ---
    factory {
            TranslationRepositoryImpl(
                systemDetails = get(),
                settingsRepository = userPreferencesRepository(),
            )
        }
        .bind<TranslationRepository>()
    userPreferencesRepositoryOf(::DataStoreSettingsRepository)
    systemDetails()

    // ---

    factoryOf(::RoomFoodDiaryEntryRepository).bind<FoodDiaryEntryRepository>()
    factoryOf(::RoomManualDiaryEntryRepository).bind<ManualDiaryEntryRepository>()
    factoryOf(::RoomMealRepository).bind<MealRepository>()
    userPreferencesRepositoryOf(::DataStoreMealsPreferencesRepository)
    factoryOf(::DataStoreGoalsRepository).bind<GoalsRepository>()

    // ---
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()

    factoryOf(::RoomFoodHistoryRepository).bind<FoodHistoryRepository>()
    factoryOf(::RoomFoodMeasurementSuggestionRepository).bind<FoodMeasurementSuggestionRepository>()
    factoryOf(::RoomFoodSearchHistoryRepository).bind<FoodSearchHistoryRepository>()
    factoryOf(::RoomProductRepository).bind<ProductRepository>()
    factoryOf(::RoomRecipeRepository).bind<RecipeRepository>()
    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    factoryOf(::ComposeSwissFoodCompositionDatabaseRepository)
        .bind<SwissFoodCompositionDatabaseRepository>()

    factoryOf(::RoomFoodSearchRepository).bind<FoodSearchRepository>()
    userPreferencesRepositoryOf(::DataStoreFoodSearchPreferencesRepository)

    factoryOf(::RemoteProductMapper)

    single(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }
    factory {
        OpenFoodFactsRemoteDataSource(
            client = get(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)),
            get(),
            get(),
        )
    }
    factoryOf(::OpenFoodFactsFacade)
    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()

    single(named(USDARemoteDataSource::class.qualifiedName!!)) {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
    factory {
        USDARemoteDataSource(
            client = get(named(USDARemoteDataSource::class.qualifiedName!!)),
            get(),
            get(),
        )
    }
    factory {
        USDAFacade(
            dataSource = get(),
            mapper = get(),
            preferencesRepository = userPreferencesRepository(),
            logger = get(),
        )
    }
    factoryOf(::USDAProductMapper)
    factory {
            USDARemoteMediatorFactory(
                foodSearchPreferencesRepository = userPreferencesRepository(),
                transactionProvider = get(),
                productRepository = get(),
                historyRepository = get(),
                remoteDataSource = get(),
                usdaHelper = get(),
                usdaMapper = get(),
                remoteMapper = get(),
                dateProvider = get(),
                logger = get(),
            )
        }
        .bind<ProductRemoteMediatorFactory>()

    factoryOf(::FoodRemoteMediatorFactoryAggregateImpl).bind<FoodRemoteMediatorFactoryAggregate>()
}
