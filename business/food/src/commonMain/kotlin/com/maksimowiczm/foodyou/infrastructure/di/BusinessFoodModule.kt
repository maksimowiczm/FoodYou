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
    factoryOf(::RoomProductRepository).bind<ProductRepository>()
    factoryOf(::RoomRecipeRepository).bind<RecipeRepository>()
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()
    factoryOf(::RoomFoodHistoryRepository).bind<FoodHistoryRepository>()
    factoryOf(::RoomFoodMeasurementSuggestionRepository).bind<FoodMeasurementSuggestionRepository>()
    factoryOf(::RoomFoodSearchRepository).bind<FoodSearchRepository>()
    factoryOf(::DataStoreFoodSearchPreferencesRepository) {
            qualifier = foodSearchPreferencesQualifier
        }
        .bind<UserPreferencesRepository<FoodSearchPreferences>>()
    factoryOf(::RoomFoodSearchHistoryRepository).bind<FoodSearchHistoryRepository>()
    factoryOf(::ComposeSwissFoodCompositionDatabaseRepository)
        .bind<SwissFoodCompositionDatabaseRepository>()

    factoryOf(::RemoteProductMapper)
    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    // TODO
    //  eventHandlerOf(::FoodDiaryEntryCreatedEventHandler)
    eventHandlerOf(::FoodSearchEventHandler)

    factoryOf(::CreateProductUseCase)
    factoryOf(::CreateRecipeUseCase)
    factoryOf(::DeleteFoodUseCase)
    factoryOf(::ObserveFoodUseCase)
    factoryOf(::UpdateProductUseCase)
    factoryOf(::UpdateRecipeUseCase)
    factoryOf(::ExportCsvProductsUseCaseImpl).bind<ExportCsvProductsUseCase>()
    factoryOf(::ImportCsvProductUseCaseImpl).bind<ImportCsvProductUseCase>()
    factoryOf(::ObserveMeasurementSuggestionsUseCase)
    factory {
            FoodSearchUseCase(
                foodSearchRepository = get(),
                foodSearchPreferencesRepository = get(),
                openFoodFactsRemoteMediatorFactory =
                    get(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)),
                usdaRemoteMediatorFactory = get(named(USDARemoteDataSource::class.qualifiedName!!)),
                dateProvider = get(),
                eventBus = get(),
            )
        }
        .bind<FoodSearchUseCase>()
    factoryOf(::DownloadProductUseCase)
    factoryOf(::ImportSwissFoodCompositionDatabaseUseCaseImpl)
        .bind<ImportSwissFoodCompositionDatabaseUseCase>()

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
    factoryOf(::USDAFacade)
    factoryOf(::USDAProductMapper)
    factoryOf(::USDARemoteMediatorFactory) {
            qualifier = named(USDARemoteDataSource::class.qualifiedName!!)
        }
        .bind<ProductRemoteMediatorFactory>()
}
