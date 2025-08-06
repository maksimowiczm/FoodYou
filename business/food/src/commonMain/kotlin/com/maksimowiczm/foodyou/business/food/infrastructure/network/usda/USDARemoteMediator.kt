package com.maksimowiczm.foodyou.business.food.infrastructure.network.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maksimowiczm.foodyou.business.food.application.command.CreateProductCommand
import com.maksimowiczm.foodyou.business.food.application.command.CreateProductError
import com.maksimowiczm.foodyou.business.food.domain.FoodEvent
import com.maksimowiczm.foodyou.business.food.domain.FoodId
import com.maksimowiczm.foodyou.business.food.domain.USDAPagingKey
import com.maksimowiczm.foodyou.business.food.infrastructure.network.RemoteProductMapper
import com.maksimowiczm.foodyou.business.food.infrastructure.persistence.LocalUsdaPagingHelper
import com.maksimowiczm.foodyou.feature.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.feature.usda.model.Food
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediator<T : Any>(
    private val remoteDataSource: USDARemoteDataSource,
    private val query: String,
    private val apiKey: String?,
    private val commandBus: CommandBus,
    private val usdaHelper: LocalUsdaPagingHelper,
    private val productMapper: USDAProductMapper,
    private val remoteMapper: RemoteProductMapper,
) : RemoteMediator<Int, T>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, T>): MediatorResult {
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
                    remoteProduct.toCommand().also {
                        if (it == null) {
                            FoodYouLogger.d(TAG) {
                                "Failed to convert product: (name=${remoteProduct.description}, code = ${remoteProduct.barcode})"
                            }
                        }
                    }
                }

            products.filterNotNull().forEach { cmd ->
                commandBus.dispatch<FoodId, CreateProductError>(cmd)
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

    private fun Food.toCommand(now: LocalDateTime = LocalDateTime.now()): CreateProductCommand? =
        runCatching {
                this.let(productMapper::toRemoteProduct).let(remoteMapper::toModel).let { product ->
                    CreateProductCommand(
                        name = product.name,
                        brand = product.brand,
                        barcode = product.barcode,
                        note = product.note,
                        isLiquid = product.isLiquid,
                        packageWeight = product.packageWeight,
                        servingWeight = product.servingWeight,
                        source = product.source,
                        nutritionFacts = product.nutritionFacts,
                        event = FoodEvent.Downloaded(now, product.source.url),
                    )
                }
            }
            .getOrNull()

    private companion object {
        private const val TAG = "USDARemoteMediator"
        private const val PAGE_SIZE = 50
    }
}
