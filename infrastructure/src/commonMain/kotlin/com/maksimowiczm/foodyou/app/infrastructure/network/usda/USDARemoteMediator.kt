package com.maksimowiczm.foodyou.app.infrastructure.network.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.app.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.app.infrastructure.network.usda.model.Food
import com.maksimowiczm.foodyou.food.domain.entity.FoodHistory
import com.maksimowiczm.foodyou.food.domain.entity.Product
import com.maksimowiczm.foodyou.food.domain.repository.FoodHistoryRepository
import com.maksimowiczm.foodyou.food.domain.repository.ProductRepository
import com.maksimowiczm.foodyou.shared.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.date.DateProvider
import com.maksimowiczm.foodyou.shared.log.Logger
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediator<K : Any, T : Any>(
    private val query: String,
    private val apiKey: String?,
    private val transactionProvider: TransactionProvider,
    private val productRepository: ProductRepository,
    private val historyRepository: FoodHistoryRepository,
    private val remoteDataSource: USDARemoteDataSource,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val productMapper: USDAProductMapper,
    private val remoteMapper: RemoteProductMapper,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : RemoteMediator<K, T>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<K, T>): MediatorResult {
        return try {
            val page =
                when (loadType) {
                    LoadType.REFRESH ->
                        return MediatorResult.Success(endOfPaginationReached = false)

                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        val pagingKey = usdaHelper.getPagingKey(query)
                        if (pagingKey != null && pagingKey.totalCount <= pagingKey.fetchedCount) {
                            logger.d(TAG) { "No more pages to load for query: $query" }
                            return MediatorResult.Success(endOfPaginationReached = true)
                        }
                        (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1
                    }
                }

            val response =
                remoteDataSource.queryProducts(
                    query = query,
                    page = page,
                    pageSize = PAGE_SIZE,
                    apiKey = apiKey,
                )

            val fetchedCount = ((response.currentPage - 1) * PAGE_SIZE) + response.foods.size

            usdaHelper.upsertPagingKey(
                USDAPagingKey(
                    queryString = query,
                    fetchedCount = fetchedCount,
                    totalCount = response.totalHits,
                )
            )

            val products =
                response.foods.map { remoteProduct ->
                    remoteProduct.toDomainProduct().also {
                        if (it == null) {
                            logger.d(TAG) {
                                "Failed to convert product: (name=${remoteProduct.description}, code = ${remoteProduct.barcode})"
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
                logger.d(TAG) { "All products skipped, trying to load next page" }
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            logger.e(TAG, e) { "Error loading page" }
            MediatorResult.Error(e)
        }
    }

    private fun Food.toDomainProduct(): Product? =
        runCatching { this.let(productMapper::toRemoteProduct).let(remoteMapper::toModel) }
            .getOrNull()

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
            historyRepository.insert(
                foodId = id,
                history = FoodHistory.Downloaded(date = now, url = this.source.url),
            )
        }
    }

    private companion object {
        private const val TAG = "USDARemoteMediator"
        private const val PAGE_SIZE = 50
    }
}
