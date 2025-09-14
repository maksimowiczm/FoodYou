package com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts

import com.maksimowiczm.foodyou.app.business.opensource.domain.search.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsFacade
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsRateLimiter
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network.OpenFoodFactsRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.room.RoomOpenFoodFactsPagingHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.onClose

internal fun Module.openFoodFactsModule() {
    factoryOf(::RoomOpenFoodFactsPagingHelper).bind<LocalOpenFoodFactsPagingHelper>()

    single(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)) {
            HttpClient {
                install(HttpTimeout)
                install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            }
        }
        .onClose { it?.close() }
    singleOf(::OpenFoodFactsRateLimiter)
    factory {
        OpenFoodFactsRemoteDataSource(
            client = get(named(OpenFoodFactsRemoteDataSource::class.qualifiedName!!)),
            get(),
            get(),
            get(),
        )
    }
    factoryOf(::OpenFoodFactsFacade)
    factoryOf(::OpenFoodFactsProductMapper)
    factoryOf(::OpenFoodFactsRemoteMediatorFactory).bind<ProductRemoteMediatorFactory>()
}
