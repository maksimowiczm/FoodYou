package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalOpenFoodFactsPagingHelper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.ProductNotFoundException
import com.maksimowiczm.foodyou.externaldatabase.openfoodfacts.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator<K : Any, T : Any>(
    private val query: String,
    private val country: String?,
    private val isBarcode: Boolean,
    private val transactionProvider: DatabaseTransactionProvider,
    private val localProduct: LocalProductDataSource,
    private val localFoodEvent: LocalFoodEventDataSource,
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val offMapper: OpenFoodFactsProductMapper,
    private val remoteMapper: RemoteProductMapper,
    private val dateProvider: DateProvider,
) : RemoteMediator<K, T>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<K, T>): MediatorResult {
        try {
            val page =
                when (loadType) {
                    LoadType.REFRESH -> {
                        // Currently there is no way to refresh the data other than delete all and
                        // fetch again.
                        return MediatorResult.Success(endOfPaginationReached = false)
                    }

                    LoadType.PREPEND -> {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    // Handle barcode search as a special case.
                    LoadType.APPEND if (isBarcode) -> {
                        val response =
                            remoteDataSource
                                .getProduct(barcode = query, countries = country)
                                .getOrElse {
                                    return if (it is ProductNotFoundException) {
                                        MediatorResult.Success(endOfPaginationReached = true)
                                    } else {
                                        MediatorResult.Error(it)
                                    }
                                }

                        val product = response.toDomainProduct()

                        if (product != null) {
                            transactionProvider.withTransaction { product.insert() }
                        } else {
                            FoodYouLogger.d(TAG) {
                                "Failed to convert product: (name=${response.name}, code=${response.barcode})"
                            }
                        }

                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    LoadType.APPEND -> {
                        val pagingKey =
                            openFoodFactsPagingHelper.getPagingKey(query = query, country = country)

                        if (pagingKey != null && pagingKey.totalCount == pagingKey.fetchedCount) {
                            FoodYouLogger.d(TAG) {
                                "No more pages to load for query: $query, country: $country"
                            }
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }

                        val nextPage = (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1

                        nextPage
                    }
                }

            FoodYouLogger.d(TAG) { "Loading page $page" }

            val response =
                remoteDataSource.queryProducts(
                    query = query,
                    countries = country,
                    page = page,
                    pageSize = PAGE_SIZE,
                )

            val fetchedCount =
                ((response.page - 1) * response.pageSize).coerceAtLeast(0) + response.products.size

            openFoodFactsPagingHelper.upsertPagingKey(
                OpenFoodFactsPagingKey(
                    queryString = query,
                    country = country,
                    fetchedCount = fetchedCount,
                    totalCount = response.count,
                )
            )

            val now = dateProvider.now()
            val products =
                response.products.map { remoteProduct ->
                    remoteProduct.toDomainProduct().also {
                        if (it == null) {
                            FoodYouLogger.d(TAG) {
                                "Failed to convert product: (name=${remoteProduct.name}, code=${remoteProduct.barcode})"
                            }
                        }
                    }
                }

            transactionProvider.withTransaction {
                products.filterNotNull().forEach { product -> product.insert() }
            }

            val skipped = products.count { it == null }
            val endOfPaginationReached = (products.size + skipped) < PAGE_SIZE

            // Load until there is anything inserted
            return if (skipped == PAGE_SIZE) {
                FoodYouLogger.d(TAG) { "All products skipped, trying to load next page" }
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            FoodYouLogger.e(TAG, e) { "Error loading page" }
            return MediatorResult.Error(e)
        }
    }

    private fun OpenFoodFactsProduct.toDomainProduct(): Product? =
        runCatching { this.let(offMapper::toRemoteProduct)?.let(remoteMapper::toModel) }.getOrNull()

    private suspend fun Product.insert(now: LocalDateTime = dateProvider.now()) {
        val id = localProduct.insertUniqueProduct(this)

        if (id != null) {
            localFoodEvent.insert(
                foodId = id,
                event = FoodEvent.Downloaded(date = now, url = this.source.url),
            )
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"

        // Feeling good about this page size might adjust later
        private const val PAGE_SIZE = 50
    }
}
