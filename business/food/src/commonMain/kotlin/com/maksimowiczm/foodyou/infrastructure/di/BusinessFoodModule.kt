package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.food.application.command.CreateProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.CreateRecipeCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.DeleteFoodCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.DownloadProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateProductCommandHandler
import com.maksimowiczm.foodyou.business.food.application.command.UpdateRecipeCommandHandler
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodQueryHandler
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodQueryHandler
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
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.FoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.datastore.DataStoreFoodPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.module.dsl.factoryOf
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

    factoryOf(::CreateProductCommandHandler) { named("CreateProductCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::DeleteFoodCommandHandler) { named("DeleteProductHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::CreateRecipeCommandHandler) { named("CreateRecipeCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::ObserveFoodQueryHandler) { named("ObserveFoodQueryHandler") }
        .bind<QueryHandler<*, *>>()
    factory(named("SearchFoodQueryHandler")) {
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
    factoryOf(::DownloadProductCommandHandler) { named("DownloadProductCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateProductCommandHandler) { named("UpdateProductCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateRecipeCommandHandler) { named("UpdateRecipeCommandHandler") }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::DataStoreFoodPreferencesDataSource).bind<FoodPreferencesDataSource>()

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
