package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.business.food.domain.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.shared.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.date.DateProvider
import com.maksimowiczm.foodyou.shared.log.Logger
import com.maksimowiczm.foodyou.shared.search.SearchQuery
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediatorFactory(
    private val foodSearchPreferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val historyRepository: FoodHistoryRepository,
    private val remoteDataSource: USDARemoteDataSource,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val usdaMapper: USDAProductMapper,
    private val remoteMapper: RemoteProductMapper,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : ProductRemoteMediatorFactory {
    override suspend fun <K : Any, T : Any> create(
        query: SearchQuery,
        pageSize: Int,
    ): RemoteMediator<K, T>? {
        if (query !is SearchQuery.NotBlank) {
            return null
        }

        return USDARemoteMediator(
            query = query.query,
            apiKey = foodSearchPreferencesRepository.observe().first().usda.apiKey,
            transactionProvider = transactionProvider,
            productRepository = productRepository,
            historyRepository = historyRepository,
            remoteDataSource = remoteDataSource,
            usdaHelper = usdaHelper,
            productMapper = usdaMapper,
            remoteMapper = remoteMapper,
            dateProvider = dateProvider,
            logger = logger,
        )
    }
}
