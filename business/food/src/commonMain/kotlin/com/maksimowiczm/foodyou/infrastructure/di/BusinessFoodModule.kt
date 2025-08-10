package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.application.command.CreateProductCommand
import com.maksimowiczm.foodyou.business.food.application.command.CreateProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.CreateRecipeCommand
import com.maksimowiczm.foodyou.business.food.application.command.CreateRecipeCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.DeleteFoodCommand
import com.maksimowiczm.foodyou.business.food.application.command.DeleteFoodCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductCommand
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateProductCommand
import com.maksimowiczm.foodyou.business.food.application.command.UpdateProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateRecipeCommand
import com.maksimowiczm.foodyou.business.food.application.command.UpdateRecipeCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUsdaApiKeyCommand
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUsdaApiKeyCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseOpenFoodFactsCommand
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseOpenFoodFactsCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseUsda
import com.maksimowiczm.foodyou.business.food.application.command.UpdateUseUsdaCommandHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodEventsQuery
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodEventsQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodPreferencesQuery
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodPreferencesQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodQuery
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveSearchHistoryQuery
import com.maksimowiczm.foodyou.business.food.application.query.ObserveSearchHistoryQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodCountQuery
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodCountQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodQuery
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodCount
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodCountQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodQuery
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
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalRecipeDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomRecipeDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.room.RoomUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.datastore.DataStoreFoodPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private const val BG_COROUTINE_SCOPE_NAME = "FoodCommandHandlersBgScope"

val businessFoodModule = module {
    single(named(BG_COROUTINE_SCOPE_NAME)) {
            CoroutineScope(
                Dispatchers.Default + SupervisorJob() + CoroutineName(BG_COROUTINE_SCOPE_NAME)
            )
        }
        .onClose { it?.cancel() }

    factoryOf(::CreateProductCommandHandler) { named(CreateProductCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::DeleteFoodCommandHandler) { named(DeleteFoodCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::CreateRecipeCommandHandler) { named(CreateRecipeCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::ObserveFoodQueryHandler) { named(ObserveFoodQuery::class.qualifiedName!!) }
        .bind<QueryHandler<*, *>>()
    factory(named(SearchFoodQuery::class.qualifiedName!!)) {
            SearchFoodQueryHandler(
                get(named(BG_COROUTINE_SCOPE_NAME)),
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
        .bind<QueryHandler<*, *>>()
    factoryOf(::DownloadProductCommandHandler) {
            named(DownloadProductCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateProductCommandHandler) { named(UpdateProductCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateRecipeCommandHandler) { named(UpdateRecipeCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::SearchFoodCountQueryHandler) { named(SearchFoodCountQuery::class.qualifiedName!!) }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveFoodPreferencesQueryHandler) {
            named(ObserveFoodPreferencesQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveSearchHistoryQueryHandler) {
            named(ObserveSearchHistoryQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::SearchRecentFoodQueryHandler) {
            named(SearchRecentFoodQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::SearchRecentFoodCountQueryHandler) {
            named(SearchRecentFoodCount::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::UpdateUseOpenFoodFactsCommandHandler) {
            named(UpdateUseOpenFoodFactsCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateUseUsdaCommandHandler) { named(UpdateUseUsda::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::ObserveFoodEventsQueryHandler) {
            named(ObserveFoodEventsQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::UpdateUsdaApiKeyCommandHandler) {
            named(UpdateUsdaApiKeyCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::DataStoreFoodPreferencesDataSource).bind<LocalFoodPreferencesDataSource>()

    factoryOf(::RoomProductDataSource).bind<LocalProductDataSource>()
    factoryOf(::RoomRecipeDataSource).bind<LocalRecipeDataSource>()
    factoryOf(::RoomFoodSearchDataSource).bind<LocalFoodSearchDataSource>()
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()
    factoryOf(::RoomFoodEventDataSource).bind<LocalFoodEventDataSource>()

    factoryOf(::RemoteProductMapper)
    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::USDAProductMapper)

    factoryOf(::OpenFoodFactsFacade)
    factoryOf(::USDAFacade)

    factoryOf(::RemoteProductRequestFactoryImpl).bind<RemoteProductRequestFactory>()
}
