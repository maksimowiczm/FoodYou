package com.maksimowiczm.foodyou.app.infrastructure.opensource.usda

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepository
import com.maksimowiczm.foodyou.app.business.shared.domain.search.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDAFacade
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDAProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDARemoteDataSource
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.network.USDARemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.opensource.usda.room.RoomUsdaPagingHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.USDAModule() {
    factoryOf(::RoomUsdaPagingHelper).bind<LocalUsdaPagingHelper>()

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
}
