package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.USDAPagingKey
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalFoodEventDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalProductDataSource
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.externaldatabase.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.externaldatabase.usda.model.Food
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediator<K : Any, T : Any>(
    private val query: String,
    private val apiKey: String?,
    private val transactionProvider: DatabaseTransactionProvider,
    private val localProduct: LocalProductDataSource,
    private val localFoodEvent: LocalFoodEventDataSource,
    private val remoteDataSource: USDARemoteDataSource,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val productMapper: USDAProductMapper,
    private val remoteMapper: RemoteProductMapper,
    private val dateProvider: DateProvider,
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
                            FoodYouLogger.d(TAG) { "No more pages to load for query: $query" }
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
                            FoodYouLogger.d(TAG) {
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
                FoodYouLogger.d(TAG) { "All products skipped, trying to load next page" }
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            FoodYouLogger.e(TAG, e) { "Error loading page" }
            MediatorResult.Error(e)
        }
    }

    private fun Food.toDomainProduct(): Product? =
        runCatching { this.let(productMapper::toRemoteProduct).let(remoteMapper::toModel) }
            .getOrNull()

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
        private const val TAG = "USDARemoteMediator"
        private const val PAGE_SIZE = 50
    }
}
