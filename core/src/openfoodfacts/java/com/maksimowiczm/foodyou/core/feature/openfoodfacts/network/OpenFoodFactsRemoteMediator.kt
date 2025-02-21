package com.maksimowiczm.foodyou.core.feature.openfoodfacts.network

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.data.model.toEntity
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.database.OpenFoodFactsDao
import com.maksimowiczm.foodyou.core.feature.openfoodfacts.database.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.core.feature.product.database.ProductEntity
import com.maksimowiczm.foodyou.core.feature.product.network.ProductRemoteMediator

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator(
    private val isBarcode: Boolean,
    private val query: String,
    private val country: String,
    private val openFoodFactsDao: OpenFoodFactsDao,
    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource
) : ProductRemoteMediator() {
    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    // Currently there is no way to refresh the data other than delete all and fetch again.
                    return MediatorResult.Success(endOfPaginationReached = false)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                // Handle barcode search as a special case.
                LoadType.APPEND if (isBarcode) -> {
                    val product = openFoodFactsNetworkDataSource.getProduct(
                        code = query,
                        country = country
                    )?.toEntity()

                    if (product != null) {
                        openFoodFactsDao.upsertOpenFoodFactsProducts(listOf(product))
                    }

                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val pagingKey = openFoodFactsDao.getPagingKey(
                        query = query,
                        country = country
                    )

                    if (pagingKey != null && pagingKey.totalCount == pagingKey.fetchedCount) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val nextPage = (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1

                    nextPage
                }
            }

            Log.d(TAG, "Loading page $page")

            val response = openFoodFactsNetworkDataSource.queryProducts(
                query = query,
                country = country,
                page = page,
                pageSize = PAGE_SIZE
            )

            val fetchedCount =
                ((response.page - 1) * response.pageSize).coerceAtLeast(0) + response.products.size

            openFoodFactsDao.upsertPagingKey(
                OpenFoodFactsPagingKey(
                    queryString = query,
                    country = country,
                    fetchedCount = fetchedCount,
                    totalCount = response.count
                )
            )

            val products = response.products.map { remoteProduct ->
                remoteProduct.toEntity().also {
                    if (it == null) {
                        Log.w(
                            TAG,
                            "Failed to convert product: (name=${remoteProduct.productName}, code=${remoteProduct.code})"
                        )
                    }
                }
            }

            openFoodFactsDao.upsertOpenFoodFactsProducts(products.filterNotNull())

            val skipped = products.count { it == null }

            val endOfPaginationReached = (products.size + skipped) < PAGE_SIZE

            return MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load page", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"

        // Feeling good about this page size might adjust later
        private const val PAGE_SIZE = 50
    }
}
