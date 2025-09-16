package com.maksimowiczm.foodyou.app.infrastructure.opensource.openfoodfacts.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.app.business.shared.domain.search.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.app.infrastructure.opensource.food.network.RemoteProductMapper
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import com.maksimowiczm.foodyou.shared.domain.search.SearchQuery

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediatorFactory(
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val foodHistoryRepository: FoodHistoryRepository,
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val offMapper: OpenFoodFactsProductMapper,
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

        return OpenFoodFactsRemoteMediator(
            query = query.query,
            country = null,
            isBarcode = query is SearchQuery.Barcode,
            transactionProvider = transactionProvider,
            productRepository = productRepository,
            foodHistoryRepository = foodHistoryRepository,
            remoteDataSource = remoteDataSource,
            openFoodFactsPagingHelper = openFoodFactsPagingHelper,
            offMapper = offMapper,
            remoteMapper = remoteMapper,
            dateProvider = dateProvider,
            logger = logger,
        )
    }
}
