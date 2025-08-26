package com.maksimowiczm.foodyou.business.food.application.query

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchDomainEvent
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.queryType
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediator
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDARemoteMediator
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.LocalFoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.query.Query
import com.maksimowiczm.foodyou.business.shared.application.query.QueryHandler
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

data class SearchFoodQuery(
    val query: String?,
    val source: FoodSource.Type,
    val excludedRecipeId: FoodId.Recipe?,
) : Query<PagingData<FoodSearch>>

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class SearchFoodQueryHandler(
    private val eventBus: EventBus,
    private val localFoodSearch: LocalFoodSearchDataSource,
    private val localFoodPreferences: LocalFoodPreferencesDataSource,
    private val localProduct: LocalProductDataSource,
    private val localFoodEvent: LocalFoodEventDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
    private val offRemoteDataSource: OpenFoodFactsRemoteDataSource,
    private val offMapper: OpenFoodFactsProductMapper,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val remoteMapper: RemoteProductMapper,
    private val usdaRemoteDataSource: USDARemoteDataSource,
    private val usdaMapper: USDAProductMapper,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val dateProvider: DateProvider,
) : QueryHandler<SearchFoodQuery, PagingData<FoodSearch>> {

    override fun handle(query: SearchFoodQuery): Flow<PagingData<FoodSearch>> {
        val (query, source, excludedRecipeId) = query

        val queryType = queryType(query)

        if (queryType is QueryType.NotBlank.Text) {
            eventBus.publish(
                FoodSearchDomainEvent(queryType = queryType, date = dateProvider.now())
            )
        }

        return localFoodPreferences.observe().flatMapLatest { prefs ->
            val mediatorFactory = mediatorFactory(queryType, source, prefs)

            localFoodSearch.search(
                query = queryType,
                source = source,
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediatorFactory = mediatorFactory,
                excludedRecipeId = excludedRecipeId?.id,
            )
        }
    }

    private fun mediatorFactory(
        query: QueryType,
        source: FoodSource.Type,
        prefs: FoodPreferences,
    ): RemoteMediatorFactory? =
        when (source) {
            FoodSource.Type.OpenFoodFacts if
                (prefs.openFoodFacts.enabled && query is QueryType.NotBlank)
             -> openFoodFactsRemoteMediatorFactory(query)

            FoodSource.Type.USDA if (prefs.usda.enabled && query is QueryType.NotBlank) ->
                usdaRemoteMediatorFactory(query, prefs.usda.apiKey)

            else -> null
        }

    private fun openFoodFactsRemoteMediatorFactory(query: QueryType.NotBlank) =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? =
                OpenFoodFactsRemoteMediator(
                    query = query.query,
                    country = null,
                    isBarcode = query is QueryType.NotBlank.Barcode,
                    transactionProvider = transactionProvider,
                    localProduct = localProduct,
                    localFoodEvent = localFoodEvent,
                    remoteDataSource = offRemoteDataSource,
                    openFoodFactsPagingHelper = openFoodFactsPagingHelper,
                    offMapper = offMapper,
                    remoteMapper = remoteMapper,
                    dateProvider = dateProvider,
                )
        }

    private fun usdaRemoteMediatorFactory(
        query: QueryType.NotBlank,
        apiKey: String?,
    ): RemoteMediatorFactory? =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? =
                USDARemoteMediator(
                    query = query.query,
                    apiKey = apiKey,
                    transactionProvider = transactionProvider,
                    localProduct = localProduct,
                    localFoodEvent = localFoodEvent,
                    remoteDataSource = usdaRemoteDataSource,
                    usdaHelper = usdaHelper,
                    productMapper = usdaMapper,
                    remoteMapper = remoteMapper,
                    dateProvider = dateProvider,
                )
        }

    private companion object {
        const val PAGE_SIZE = 30
    }
}
