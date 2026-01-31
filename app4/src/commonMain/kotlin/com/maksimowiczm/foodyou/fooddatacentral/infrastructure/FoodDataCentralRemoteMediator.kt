package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.room.immediateTransaction
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralPagingKeyEntity
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralProductEntity
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
internal class FoodDataCentralRemoteMediator(
    private val query: SearchQuery.NotBlank,
    private val database: FoodDataCentralDatabase,
    private val remote: FoodDataCentralRemoteDataSource,
    private val apiKey: String?,
    logger: Logger,
) : RemoteMediator<Int, FoodDataCentralProductEntity>() {
    private val logger = logger.withTag(TAG)
    private val dao = database.dao
    private val mapper = FoodDataCentralProductMapper()

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, FoodDataCentralProductEntity>,
    ): MediatorResult {
        try {
            val page =
                when (loadType) {
                    LoadType.REFRESH ->
                        return MediatorResult.Success(endOfPaginationReached = false)

                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                    LoadType.APPEND ->
                        when (query) {
                            is SearchQuery.Text,
                            is SearchQuery.Barcode -> {
                                val count = dao.getPagingKeyCountByQuery(query.query)
                                val nextPage = (count / PAGE_SIZE) + 1
                                nextPage
                            }

                            is SearchQuery.OpenFoodFactsUrl ->
                                return MediatorResult.Success(endOfPaginationReached = true)

                            is SearchQuery.FoodDataCentralUrl -> {
                                val existingProduct = dao.observeCountByFdcId(query.fdcId).first()
                                if (existingProduct > 0) {
                                    return MediatorResult.Success(endOfPaginationReached = true)
                                }

                                val response =
                                    remote.getProduct(query.fdcId, apiKey).getOrElse {
                                        return if (it is FoodDataCentralApiError.ProductNotFound)
                                            MediatorResult.Success(endOfPaginationReached = true)
                                        else MediatorResult.Error(it)
                                    }

                                val product = mapper.foodDataCentralProductEntity(response)
                                dao.upsertProduct(product)
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }
                        }
                }

            logger.d { "Loading page $page" }

            val response =
                remote.queryProducts(
                    query = query.query,
                    page = page,
                    apiKey = apiKey,
                    pageSize = PAGE_SIZE,
                )

            val entities = response.foods.map(mapper::foodDataCentralProductEntity)
            val pagingKeys =
                entities.map {
                    FoodDataCentralPagingKeyEntity(queryString = query.query, fdcId = it.fdcId)
                }

            database.immediateTransaction {
                dao.upsertProducts(entities)
                dao.insertPagingKeys(pagingKeys)
            }

            val skipped = response.foods.size - entities.size
            val endOfPaginationReached = entities.size + skipped < PAGE_SIZE

            return if (skipped == PAGE_SIZE) {
                logger.w { "All products on page were skipped" }
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            logger.e("Error during loading data from OpenFoodFacts", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "FoodDataCentralRemoteMediator"
        private const val PAGE_SIZE = 200
    }
}
