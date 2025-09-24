package com.maksimowiczm.foodyou.food.search.infrastructure.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.common.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferencesRepository
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.food.infrastructure.usda.USDAProductMapper
import com.maksimowiczm.foodyou.food.infrastructure.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferences
import com.maksimowiczm.foodyou.food.search.domain.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.food.search.infrastructure.room.USDAPagingKeyDao
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediatorFactory(
    private val foodSearchPreferencesRepository: UserPreferencesRepository<FoodSearchPreferences>,
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val historyRepository: FoodHistoryRepository,
    private val remoteDataSource: USDARemoteDataSource,
    private val pagingKeyDao: USDAPagingKeyDao,
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
            pagingKeyDao = pagingKeyDao,
            productMapper = usdaMapper,
            remoteMapper = remoteMapper,
            dateProvider = dateProvider,
            logger = logger,
        )
    }
}
