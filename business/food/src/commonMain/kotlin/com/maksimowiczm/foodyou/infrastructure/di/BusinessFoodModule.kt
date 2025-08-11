package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.application.command.CreateProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.CreateRecipeCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.DeleteFoodCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateRecipeCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUsdaApiKeyCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseOpenFoodFactsCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseUsdaCommandHandler
import com.maksimowiczm.foodyou.business.food.application.event.FoodDiaryEntryCreatedEventHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodEventsQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodPreferencesQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveMeasurementSuggestionsQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveSearchHistoryQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodCountQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodCountQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodQueryHandler
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequestFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductRequestFactoryImpl
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAFacade
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalMeasurementSuggestionDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomMeasurementSuggestionDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomRecipeDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.datastore.DataStoreFoodPreferencesDataSource
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodModule = module {
    commandHandlerOf(::CreateProductCommandHandler)
    commandHandlerOf(::DeleteFoodCommandHandler)
    commandHandlerOf(::CreateRecipeCommandHandler)
    commandHandlerOf(::DownloadProductCommandHandler)
    commandHandlerOf(::UpdateProductCommandHandler)
    commandHandlerOf(::UpdateRecipeCommandHandler)
    commandHandlerOf(::UpdateUsdaApiKeyCommandHandler)
    commandHandlerOf(::UpdateUseOpenFoodFactsCommandHandler)
    commandHandlerOf(::UpdateUseUsdaCommandHandler)

    queryHandlerOf(::ObserveFoodQueryHandler)
    queryHandler {
        SearchFoodQueryHandler(
            coroutineScope = applicationCoroutineScope(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    queryHandlerOf(::SearchFoodCountQueryHandler)
    queryHandlerOf(::ObserveFoodPreferencesQueryHandler)
    queryHandlerOf(::ObserveSearchHistoryQueryHandler)
    queryHandler {
        SearchRecentFoodQueryHandler(
            coroutineScope = applicationCoroutineScope(),
            get<LocalFoodSearchDataSource>(),
        )
    }
    queryHandlerOf(::SearchRecentFoodCountQueryHandler)
    queryHandlerOf(::ObserveFoodEventsQueryHandler)
    queryHandlerOf(::ObserveMeasurementSuggestionsQueryHandler)

    factoryOf(::DataStoreFoodPreferencesDataSource).bind<LocalFoodPreferencesDataSource>()

    factoryOf(::RoomProductDataSource).bind<LocalProductDataSource>()
    factoryOf(::RoomRecipeDataSource).bind<LocalRecipeDataSource>()
    factoryOf(::RoomFoodSearchDataSource).bind<LocalFoodSearchDataSource>()
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()
    factoryOf(::RoomFoodEventDataSource).bind<LocalFoodEventDataSource>()
    factoryOf(::RoomMeasurementSuggestionDataSource).bind<LocalMeasurementSuggestionDataSource>()

    factoryOf(::RemoteProductMapper)
    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::USDAProductMapper)

    factoryOf(::OpenFoodFactsFacade)
    factoryOf(::USDAFacade)

    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()

    eventHandlerOf(::FoodDiaryEntryCreatedEventHandler)
}
