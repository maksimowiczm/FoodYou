package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.application.CreateProductUseCase
import com.maksimowiczm.foodyou.business.food.application.CreateProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.CreateRecipeUseCase
import com.maksimowiczm.foodyou.business.food.application.CreateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.DeleteFoodUseCase
import com.maksimowiczm.foodyou.business.food.application.DeleteFoodUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.DownloadProductUseCase
import com.maksimowiczm.foodyou.business.food.application.DownloadProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.ExportCsvProductsUseCase
import com.maksimowiczm.foodyou.business.food.application.ExportCsvProductsUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.FoodSearchUseCase
import com.maksimowiczm.foodyou.business.food.application.FoodSearchUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.ImportCsvProductUseCase
import com.maksimowiczm.foodyou.business.food.application.ImportCsvProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.ObserveFoodUseCase
import com.maksimowiczm.foodyou.business.food.application.ObserveFoodUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.business.food.application.ObserveMeasurementSuggestionsUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.UpdateProductUseCase
import com.maksimowiczm.foodyou.business.food.application.UpdateProductUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.business.food.application.UpdateRecipeUseCaseImpl
import com.maksimowiczm.foodyou.business.food.application.event.FoodDiaryEntryCreatedEventHandler
import com.maksimowiczm.foodyou.business.food.application.event.FoodSearchEventHandler
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchRepository
import com.maksimowiczm.foodyou.business.food.domain.MeasurementSuggestionRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.RecipeRepository
import com.maksimowiczm.foodyou.business.food.domain.SearchHistoryRepository
import com.maksimowiczm.foodyou.business.food.domain.remote.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.FoodSearchRepositoryImpl
import com.maksimowiczm.foodyou.business.food.infrastructure.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.datastore.DataStoreFoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomFoodEventRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomMeasurementSuggestionRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomProductRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomRecipeRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomSearchHistoryRepository
import com.maksimowiczm.foodyou.business.food.infrastructure.room.RoomUsdaPagingHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.qualifier
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

val businessFoodModule = module {
    factoryOf(::RoomProductRepository).bind<ProductRepository>()
    factoryOf(::RoomRecipeRepository).bind<RecipeRepository>()
    factoryOf(::RoomFoodSearchDataSource).bind<LocalFoodSearchDataSource>()
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()
    factoryOf(::RoomFoodEventRepository).bind<FoodEventRepository>()
    factoryOf(::RoomMeasurementSuggestionRepository).bind<MeasurementSuggestionRepository>()
    factoryOf(::FoodSearchRepositoryImpl).bind<FoodSearchRepository>()
    factoryOf(::DataStoreFoodSearchPreferencesRepository).bind<FoodSearchPreferencesRepository>()
    factoryOf(::RoomSearchHistoryRepository).bind<SearchHistoryRepository>()

    factoryOf(::RemoteProductMapper)
    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::USDAProductMapper)

    factoryOf(::OpenFoodFactsFacade)
    factoryOf(::USDAFacade)

    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    eventHandlerOf(::FoodDiaryEntryCreatedEventHandler)
    eventHandlerOf(::FoodSearchEventHandler)

    factoryOf(::CreateProductUseCaseImpl).bind<CreateProductUseCase>()
    factoryOf(::CreateRecipeUseCaseImpl).bind<CreateRecipeUseCase>()
    factoryOf(::DeleteFoodUseCaseImpl).bind<DeleteFoodUseCase>()
    factoryOf(::ObserveFoodUseCaseImpl).bind<ObserveFoodUseCase>()
    factoryOf(::UpdateProductUseCaseImpl).bind<UpdateProductUseCase>()
    factoryOf(::UpdateRecipeUseCaseImpl).bind<UpdateRecipeUseCase>()
    factoryOf(::ExportCsvProductsUseCaseImpl).bind<ExportCsvProductsUseCase>()
    factoryOf(::ImportCsvProductUseCaseImpl).bind<ImportCsvProductUseCase>()
    factoryOf(::ObserveMeasurementSuggestionsUseCaseImpl)
        .bind<ObserveMeasurementSuggestionsUseCase>()
    factoryOf(::FoodSearchUseCaseImpl).bind<FoodSearchUseCase>()
    factoryOf(::DownloadProductUseCaseImpl).bind<DownloadProductUseCase>()

    single(qualifier("OpenFoodFactsClient")) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }
    factory {
        OpenFoodFactsRemoteDataSource(client = get(qualifier("OpenFoodFactsClient")), get(), get())
    }

    single(qualifier("USDA")) {
        HttpClient {
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }

    factory { USDARemoteDataSource(client = get(qualifier("USDA")), get(), get()) }
}
