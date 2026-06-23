package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsUrlSearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsApiV2ProductDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsSearchDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsPagingKeyDao
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class OpenFoodFactsSearchScope(
    private val pagingKeyDao: OpenFoodFactsPagingKeyDao<*>,
    private val productDao: OpenFoodFactsProductDao,
    private val searchDataSource: OpenFoodFactsSearchDataSource,
    private val mapper: OpenFoodFactsProductMapper,
    private val apiV2: OpenFoodFactsApiV2ProductDataSource,
    private val logger: Logger,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun search(
        parameters: OpenFoodFactsSearchParameters,
        pageSize: Int,
        remoteEnabled: Boolean,
        onNewFetchProduct: suspend (Set<OpenFoodFactsProduct>) -> Unit,
    ): Flow<PagingData<OpenFoodFactsProduct>> {
        val config = PagingConfig(pageSize = pageSize)

        val factory = {
            when (val query = parameters.query) {
                is OpenFoodFactsUrlSearchQuery -> productDao.getPagingSourceByBarcode(query.barcode)
                is SearchQuery.Barcode -> productDao.getPagingSourceByBarcode(query.barcode)
                is SearchQuery.Blank -> productDao.getPagingSource()
                is SearchQuery.NotBlank -> pagingKeyDao.getPagingSourceByQuery(query.query)
            }
        }

        val remoteMediator =
            if (remoteEnabled && parameters.query is SearchQuery.NotBlank) {
                OpenFoodFactsRemoteMediator(
                    query = parameters.query,
                    pagingKeyDao = pagingKeyDao,
                    productDao = productDao,
                    search = searchDataSource,
                    apiV2 = apiV2,
                    mapper = mapper,
                    pageSize = pageSize,
                    logger = logger,
                    onNewProduct = onNewFetchProduct,
                )
            } else null

        return Pager(
                config = config,
                pagingSourceFactory = factory,
                remoteMediator = remoteMediator,
            )
            .flow
            .map { data -> data.map(mapper::toModel) }
    }

    fun count(parameters: OpenFoodFactsSearchParameters): Flow<Int> =
        when (val query = parameters.query) {
            is OpenFoodFactsUrlSearchQuery -> productDao.observeCountByBarcode(query.barcode)
            is SearchQuery.Barcode -> productDao.observeCountByBarcode(query.barcode)
            is SearchQuery.Blank -> productDao.observeCount()
            is SearchQuery.NotBlank -> pagingKeyDao.observeCountByQuery(query.query)
        }
}
