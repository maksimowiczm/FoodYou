package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.ProductRepository
import com.maksimowiczm.foodyou.business.food.domain.QueryType
import com.maksimowiczm.foodyou.business.food.domain.remote.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.application.log.Logger

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediatorFactory(
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val foodEventRepository: FoodEventRepository,
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val offMapper: OpenFoodFactsProductMapper,
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

        return OpenFoodFactsRemoteMediator(
            query = query.query,
            country = null,
            isBarcode = query is QueryType.NotBlank.Barcode,
            transactionProvider = transactionProvider,
            productRepository = productRepository,
            foodEventRepository = foodEventRepository,
            remoteDataSource = remoteDataSource,
            openFoodFactsPagingHelper = openFoodFactsPagingHelper,
            offMapper = offMapper,
            remoteMapper = remoteMapper,
            dateProvider = dateProvider,
            logger = logger,
        )
    }
}
