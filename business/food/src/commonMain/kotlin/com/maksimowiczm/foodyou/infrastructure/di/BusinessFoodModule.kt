package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.application.ExportCsvProductsUseCase
import com.maksimowiczm.foodyou.business.food.application.ExportCsvProductsUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.business.food.application.ImportCsvProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.ImportSwissFoodCompositionDatabaseUseCase
import com.maksimowiczm.foodyou.business.food.application.ImportSwissFoodCompositionDatabaseUseCaseImpl
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.compose.ComposeSwissFoodCompositionDatabaseRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.datastore.DataStoreFoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDARemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomFoodHistoryRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomFoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomFoodSearchHistoryRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomFoodSearchRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomProductRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomRecipeRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomUsdaPagingHelper
import com.maksimowiczm.foodyou.food.domain.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchPreferences
import com.maksimowiczm.foodyou.food.domain.eventhandler.FoodSearchEventHandler
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.food.domain.repository.RecipeRepository
import com.maksimowiczm.foodyou.food.domain.repository.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.food.domain.usecase.CreateProductUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.CreateRecipeUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.DeleteFoodUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.FoodSearchUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.UpdateProductUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

val foodSearchPreferencesQualifier = qualifier(FoodSearchPreferences::class.qualifiedName!!)

val businessFoodModule = module {
    // Core
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()

    factoryOf(::RoomFoodHistoryRepository).bind<FoodHistoryRepository>()
    factoryOf(::RoomFoodMeasurementSuggestionRepository).bind<FoodMeasurementSuggestionRepository>()
    factoryOf(::RoomFoodSearchHistoryRepository).bind<FoodSearchHistoryRepository>()
    factoryOf(::RoomProductRepository).bind<ProductRepository>()
    factoryOf(::RoomRecipeRepository).bind<RecipeRepository>()
    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    factoryOf(::CreateProductUseCase)
    factoryOf(::CreateRecipeUseCase)
    factoryOf(::DeleteFoodUseCase)
    factoryOf(::DownloadProductUseCase)
    factoryOf(::ObserveFoodUseCase)
    factoryOf(::ObserveMeasurementSuggestionsUseCase)
    factoryOf(::UpdateProductUseCase)
    factoryOf(::UpdateRecipeUseCase)

    // App
    factoryOf(::ComposeSwissFoodCompositionDatabaseRepository)
        .bind<SwissFoodCompositionDatabaseRepository>()

    factoryOf(::ExportCsvProductsUseCaseImpl).bind<ExportCsvProductsUseCase>()
    factoryOf(::ImportCsvProductUseCaseImpl).bind<ImportCsvProductUseCase>()
    factoryOf(::ImportSwissFoodCompositionDatabaseUseCaseImpl)
        .bind<ImportSwissFoodCompositionDatabaseUseCase>()

    // Core search
    // TODO
    //  eventHandlerOf(::FoodDiaryEntryCreatedEventHandler)
    eventHandlerOf(::FoodSearchEventHandler)

    factoryOf(::RoomFoodSearchRepository).bind<FoodSearchRepository>()
    factoryOf(::DataStoreFoodSearchPreferencesRepository) {
            qualifier = foodSearchPreferencesQualifier
        }
        .bind<UserPreferencesRepository<FoodSearchPreferences>>()

    factory {
            FoodSearchUseCase(
                foodSearchRepository = get(),
                foodSearchPreferencesRepository = get(foodSearchPreferencesQualifier),
                openFoodFactsRemoteMediatorFactory =
                    get(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)),
                usdaRemoteMediatorFactory = get(named(USDARemoteDataSource::class.qualifiedName!!)),
                dateProvider = get(),
                eventBus = get(),
            )
        }
        .bind<FoodSearchUseCase>()

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
    factoryOf(::OpenFoodFactsRemoteMediatorFactory) {
            qualifier = named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)
        }
        .bind<ProductRemoteMediatorFactory>()

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
            preferencesRepository = get(foodSearchPreferencesQualifier),
            logger = get(),
        )
    }
    factoryOf(::USDAProductMapper)
    factory(qualifier = named(USDARemoteDataSource::class.qualifiedName!!)) {
            USDARemoteMediatorFactory(
                foodSearchPreferencesRepository = get(foodSearchPreferencesQualifier),
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
}
