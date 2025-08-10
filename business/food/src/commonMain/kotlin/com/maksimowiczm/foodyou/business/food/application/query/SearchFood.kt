package com.maksimowiczm.foodyou.business.food.application.query

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodPreferences
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.FoodSource
import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediator
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDARemoteMediator
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodSearchDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.preferences.datastore.DataStoreFoodPreferencesDataSource
import com.maksimowiczm.foodyou.business.shared.infrastructure.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

data class SearchFoodQuery(
    val query: String?,
    val source: FoodSource.Type,
    val excludedRecipeId: FoodId.Recipe?,
) : Query<PagingData<FoodSearch>>

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class SearchFoodQueryHandler(
    private val coroutineScope: CoroutineScope,
    private val commandBus: CommandBus,
    private val foodSearchSource: LocalFoodSearchDataSource,
    private val foodPreferencesSource: DataStoreFoodPreferencesDataSource,
    private val remoteMapper: RemoteProductMapper,
    private val offRemoteDataSource: OpenFoodFactsRemoteDataSource,
    private val offMapper: OpenFoodFactsProductMapper,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val usdaRemoteDataSource: USDARemoteDataSource,
    private val usdaMapper: USDAProductMapper,
    private val usdaHelper: LocalUsdaPagingHelper,
) : QueryHandler<SearchFoodQuery, PagingData<FoodSearch>> {

    override fun handle(query: SearchFoodQuery): Flow<PagingData<FoodSearch>> {
        val (query, source, excludedRecipeId) = query

        val queryType = queryType(query)

        if (queryType is QueryType.NotBlank.Text) {
            coroutineScope.launch {
                foodSearchSource.insertSearchHistory(
                    SearchHistory(query = queryType.query, date = LocalDateTime.now())
                )
            }
        }

        return foodPreferencesSource.observe().flatMapLatest { prefs ->
            val mediatorFactory = mediatorFactory(queryType, source, prefs)

            foodSearchSource.search(
                query = query,
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
                    remoteDataSource = offRemoteDataSource,
                    query = query.query,
                    country = null,
                    isBarcode = query is QueryType.NotBlank.Barcode,
                    commandBus = commandBus,
                    openFoodFactsPagingHelper = openFoodFactsPagingHelper,
                    offMapper = offMapper,
                    remoteMapper = remoteMapper,
                )
        }

    private fun usdaRemoteMediatorFactory(
        query: QueryType.NotBlank,
        apiKey: String?,
    ): RemoteMediatorFactory? =
        object : RemoteMediatorFactory {
            override fun <K : Any, T : Any> create(): RemoteMediator<K, T>? =
                USDARemoteMediator(
                    remoteDataSource = usdaRemoteDataSource,
                    query = query.query,
                    apiKey = apiKey,
                    commandBus = commandBus,
                    usdaHelper = usdaHelper,
                    productMapper = usdaMapper,
                    remoteMapper = remoteMapper,
                )
        }

    private companion object {
        const val PAGE_SIZE = 30
    }
}

private fun queryType(query: String?): QueryType =
    when {
        query.isNullOrBlank() -> QueryType.Blank
        query.all { it.isDigit() } -> QueryType.NotBlank.Barcode(query)
        else -> QueryType.NotBlank.Text(query)
    }

private sealed interface QueryType {
    val query: String?

    data object Blank : QueryType {
        override val query: String? = null
    }

    sealed interface NotBlank : QueryType {
        override val query: String

        data class Barcode(override val query: String) : NotBlank

        data class Text(override val query: String) : NotBlank
    }
}
