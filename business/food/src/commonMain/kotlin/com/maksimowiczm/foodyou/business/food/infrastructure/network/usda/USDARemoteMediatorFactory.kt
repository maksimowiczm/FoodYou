package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.remote.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediatorFactory(
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val foodEventRepository: FoodEventRepository,
    private val remoteDataSource: USDARemoteDataSource,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val usdaMapper: USDAProductMapper,
    private val remoteMapper: RemoteProductMapper,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : ProductRemoteMediatorFactory {
    override suspend fun <K : Any, T : Any> create(
        query: QueryType,
        pageSize: Int,
    ): RemoteMediator<K, T>? {
        if (query !is QueryType.NotBlank) {
            return null
        }

        return USDARemoteMediator(
            query = query.query,
            apiKey = foodSearchPreferencesRepository.observe().first().usda.apiKey,
            transactionProvider = transactionProvider,
            productRepository = productRepository,
            foodEventRepository = foodEventRepository,
            remoteDataSource = remoteDataSource,
            usdaHelper = usdaHelper,
            productMapper = usdaMapper,
            remoteMapper = remoteMapper,
            dateProvider = dateProvider,
            logger = logger,
        )
    }
}
