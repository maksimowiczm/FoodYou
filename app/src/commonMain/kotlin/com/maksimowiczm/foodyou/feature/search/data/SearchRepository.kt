package com.maksimowiczm.foodyou.feature.search.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.feature.search.database.SearchDatabase
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductQueryEntity
import com.maksimowiczm.foodyou.feature.search.domain.ObserveProductQueries
import com.maksimowiczm.foodyou.feature.search.domain.Product
import com.maksimowiczm.foodyou.feature.search.domain.ProductQuery
import com.maksimowiczm.foodyou.feature.search.domain.QueryProductsUseCase
import com.maksimowiczm.foodyou.feature.search.network.ProductRemoteMediatorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SearchRepository(
    searchDatabase: SearchDatabase,
    private val productRemoteMediatorFactory: ProductRemoteMediatorFactory,
    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) : QueryProductsUseCase,
    ObserveProductQueries {
    val productDao = searchDatabase.productDao
    val searchDao = searchDatabase.searchDao

    @OptIn(ExperimentalPagingApi::class)
    override fun queryProducts(query: String?): Flow<PagingData<Product>> {
        val barcode = query?.takeIf { it.all(Char::isDigit) }

        val localOnly = query == null
        val remoteMediator = when {
            localOnly -> null
            barcode != null -> productRemoteMediatorFactory.createWithBarcode(barcode)
            else -> productRemoteMediatorFactory.createWithQuery(query)
        }

        // Insert query if it's not a barcode and not empty
        if (barcode == null && query?.isNotBlank() == true) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            remoteMediator = remoteMediator
        ) {
            when {
                barcode != null -> productDao.queryProductsWithBarcode(barcode)
                query != null -> productDao.queryProductsWithQuery(query)
                else -> productDao.observeProducts()
            }
        }.flow.map { pagingData ->
            pagingData.map { it.toQueryProductsUseCaseModel() }
        }
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

        searchDao.upsertProductQuery(
            ProductQueryEntity(
                query = query,
                date = epochSeconds
            )
        )
    }

    override fun observeProductQueries(limit: Int): Flow<List<ProductQuery>> =
        searchDao.observeProductQueries(limit).map { productQueries ->
            productQueries.map { productQuery ->
                ProductQuery(
                    query = productQuery.query,
                    date = Instant.fromEpochSeconds(productQuery.date).toLocalDateTime(TimeZone.UTC)
                )
            }
        }

    private companion object {
        const val PAGE_SIZE = 30
    }
}

private fun ProductEntity.toQueryProductsUseCaseModel() = Product(
    id = id,
    name = name,
    brand = brand
)
