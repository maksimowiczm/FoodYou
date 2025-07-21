package com.maksimowiczm.foodyou.feature.food.data.network.usda

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product
import com.maksimowiczm.foodyou.feature.food.data.database.usda.USDAPagingKey
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductMapper
import com.maksimowiczm.foodyou.feature.usda.USDARemoteDataSource
import com.maksimowiczm.foodyou.feature.usda.model.Food

@OptIn(ExperimentalPagingApi::class)
internal class USDARemoteMediator<T : Any>(
    private val remoteDataSource: USDARemoteDataSource,
    foodDatabase: FoodDatabase,
    private val query: String,
    private val apiKey: String?,
    private val usdaMapper: USDAProductMapper,
    private val remoteMapper: RemoteProductMapper
) : RemoteMediator<Int, T>() {

    private val productDao = foodDatabase.productDao
    private val usdaDao = foodDatabase.usdaPagingKeyDao

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, T>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> return MediatorResult.Success(endOfPaginationReached = false)
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val pagingKey = usdaDao.getPagingKey(query)
                    if (pagingKey != null && pagingKey.totalCount <= pagingKey.fetchedCount) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1
                }
            }

            val response = remoteDataSource.queryProducts(
                query = query,
                page = page,
                pageSize = PAGE_SIZE,
                apiKey = apiKey
            )

            val fetchedCount = ((response.currentPage - 1) * PAGE_SIZE) + response.foods.size

            usdaDao.insertPagingKey(
                USDAPagingKey(
                    queryString = query,
                    fetchedCount = fetchedCount,
                    totalCount = response.totalHits
                )
            )

            val products = response.foods.map { remoteProduct ->
                remoteProduct.toEntity().also {
                    if (it == null) {
                        Logger.w(TAG) {
                            "Failed to convert product: (name=${remoteProduct.description}, code=${remoteProduct.barcode})"
                        }
                    }
                }
            }

            productDao.insertUniqueProducts(products.filterNotNull())

            val skipped = products.count { it == null }
            val endOfPaginationReached = (products.size + skipped) < PAGE_SIZE

            // Load until there is anything inserted
            return if (skipped == PAGE_SIZE) {
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Error loading page" }
            MediatorResult.Error(e)
        }
    }

    private fun Food.toEntity(): Product? = runCatching {
        val remoteProduct = usdaMapper.toRemoteProduct(this)
        val entity = remoteMapper.toEntity(remoteProduct)
        return entity
    }.getOrNull()

    private companion object {
        private const val TAG = "USDARemoteMediator"
        private const val PAGE_SIZE = 50
    }
}
