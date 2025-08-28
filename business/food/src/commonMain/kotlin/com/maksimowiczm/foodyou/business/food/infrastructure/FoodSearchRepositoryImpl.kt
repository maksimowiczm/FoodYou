package com.maksimowiczm.foodyou.business.food.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.FoodSearch
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.OpenFoodFactsRemoteMediator
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.usda.USDARemoteMediator
import com.maksimowiczm.foodyou.business.shared.application.database.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.network.RemoteMediatorFactory
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
internal class FoodSearchRepositoryImpl(
    private val transactionProvider: DatabaseTransactionProvider,
    private val productRepository: ProductRepository,
    private val foodEventRepository: FoodEventRepository,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
    private val foodSearchDataSource: LocalFoodSearchDataSource,
    private val offRemoteDataSource: OpenFoodFactsRemoteDataSource,
    private val offMapper: OpenFoodFactsProductMapper,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val remoteMapper: RemoteProductMapper,
    private val usdaRemoteDataSource: USDARemoteDataSource,
    private val usdaMapper: USDAProductMapper,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val dateProvider: DateProvider,
) : FoodSearchRepository {
    override fun searchFood(
        query: QueryType,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> =
        foodSearchPreferencesRepository.observe().flatMapLatest { prefs ->
            foodSearchDataSource.search(
                query = query,
                source = source,
                config = PagingConfig(pageSize = PAGE_SIZE),
                remoteMediatorFactory = mediatorFactory(query, source, prefs),
                excludedRecipeId = excludedRecipeId?.id,
            )
        }

    override fun observeSearchFoodCount(
        query: QueryType,
        source: FoodSource.Type,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int> =
        foodSearchDataSource.observeFoodCount(
            query = query,
            source = source,
            excludedRecipeId = excludedRecipeId?.id,
        )

    override fun searchRecentFood(
        query: QueryType,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<PagingData<FoodSearch>> =
        foodSearchDataSource.searchRecent(
            query = query,
            config = PagingConfig(PAGE_SIZE),
            excludedRecipeId = excludedRecipeId?.id,
        )

    override fun observeRecentFoodCount(
        query: QueryType,
        excludedRecipeId: FoodId.Recipe?,
    ): Flow<Int> =
        foodSearchDataSource.observeRecentFoodCount(
            query = query,
            excludedRecipeId = excludedRecipeId?.id,
        )

    private fun mediatorFactory(
        query: QueryType,
        source: FoodSource.Type,
        prefs: FoodSearchPreferences,
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
                    productRepository = productRepository,
                    foodEventRepository = foodEventRepository,
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
                    productRepository = productRepository,
                    foodEventRepository = foodEventRepository,
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
