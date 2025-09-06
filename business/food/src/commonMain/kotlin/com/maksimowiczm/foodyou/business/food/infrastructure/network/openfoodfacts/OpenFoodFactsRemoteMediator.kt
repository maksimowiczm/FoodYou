package com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.network.openfoodfacts.model.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.entity.RemoteFoodException
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.shared.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.date.DateProvider
import com.maksimowiczm.foodyou.shared.log.Logger
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator<K : Any, T : Any>(
    private val query: String,
    private val country: String?,
    private val isBarcode: Boolean,
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val foodHistoryRepository: FoodHistoryRepository,
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val openFoodFactsPagingHelper: LocalOpenFoodFactsPagingHelper,
    private val offMapper: OpenFoodFactsProductMapper,
    private val remoteMapper: RemoteProductMapper,
    private val dateProvider: DateProvider,
    private val logger: Logger,
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
                                    return if (it is RemoteFoodException.ProductNotFoundException) {
                                        MediatorResult.Success(endOfPaginationReached = true)
                                    } else {
                                        MediatorResult.Error(it)
                                    }
                                }

                        val product = response.toDomainProduct()

                        if (product != null) {
                            transactionProvider.withTransaction { product.insert() }
                        } else {
                            logger.d(TAG) {
                                "Failed to convert product: (name=${response.name}, code=${response.barcode})"
                            }
                        }

                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    LoadType.APPEND -> {
                        val pagingKey =
                            openFoodFactsPagingHelper.getPagingKey(query = query, country = country)

                        if (pagingKey != null && pagingKey.totalCount == pagingKey.fetchedCount) {
                            logger.d(TAG) {
                                "No more pages to load for query: $query, country: $country"
                            }
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }

                        val nextPage = (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1

                        nextPage
                    }
                }

            logger.d(TAG) { "Loading page $page" }

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

            val products =
                response.products.map { remoteProduct ->
                    remoteProduct.toDomainProduct().also {
                        if (it == null) {
                            logger.d(TAG) {
                                "Failed to convert product: (name=${remoteProduct.name}, code=${remoteProduct.barcode})"
                            }
                        }
                    }
                }

            val now = dateProvider.now()
            transactionProvider.withTransaction {
                products.filterNotNull().forEach { product -> product.insert(now) }
            }

            val skipped = products.count { it == null }
            val endOfPaginationReached = (products.size + skipped) < PAGE_SIZE

            // Load until there is anything inserted
            return if (skipped == PAGE_SIZE) {
                logger.d(TAG) { "All products skipped, trying to load next page" }
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            logger.e(TAG, e) { "Error loading page" }
            return MediatorResult.Error(e)
        }
    }

    private fun OpenFoodFactsProduct.toDomainProduct(): Product? =
        runCatching { this.let(offMapper::toRemoteProduct).let(remoteMapper::toModel) }.getOrNull()

    private suspend fun Product.insert(now: LocalDateTime = dateProvider.now()) {
        val id =
            productRepository.insertUniqueProduct(
                name = this.name,
                brand = this.brand,
                barcode = this.barcode,
                note = this.note,
                isLiquid = this.isLiquid,
                packageWeight = this.packageWeight,
                servingWeight = this.servingWeight,
                source = this.source,
                nutritionFacts = this.nutritionFacts,
            )

        if (id != null) {
            foodHistoryRepository.insert(
                foodId = id,
                history = FoodHistory.Downloaded(date = now, url = this.source.url),
            )
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"

        // Feeling good about this page size might adjust later
        private const val PAGE_SIZE = 50
    }
}
