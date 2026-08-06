package com.maksimowiczm.foodyou.fooddatacentral.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralApiError
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters.DataType
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralUrlSearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.network.FoodDataCentralRemoteDataSource
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralDatabase
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralPagingKeyEntity
import com.maksimowiczm.foodyou.fooddatacentral.infrastructure.room.FoodDataCentralProductEntity
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
internal class FoodDataCentralRemoteMediator(
    private val query: SearchQuery.NotBlank,
    database: FoodDataCentralDatabase,
    private val remote: FoodDataCentralRemoteDataSource,
    private val mapper: FoodDataCentralProductMapper,
    private val apiKey: String?,
    private val pageSize: Int,
    private val dataTypes: Set<DataType>?,
    logger: Logger,
    private val onNewProduct: suspend (Set<FoodDataCentralProduct>) -> Unit,
) : RemoteMediator<Int, FoodDataCentralProductEntity>() {
    private val logger = logger.withTag(TAG)
    private val dao = database.dao
    private val dataTypeFilters = dataTypes?.map { it.filter }

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
                            is FoodDataCentralUrlSearchQuery -> {
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
                                dao.upsertProductAndGet(product)?.let {
                                    onNewProduct(setOf(mapper.foodDataCentralProduct(it)))
                                }
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }

                            is SearchQuery.NotBlank -> {
                                val count = dao.getPagingKeyCountByQuery(query.query, dataTypes)
                                val nextPage = (count / pageSize) + 1
                                nextPage
                            }
                        }
                }

            logger.d { "Loading page $page" }

            val response =
                remote.queryProducts(
                    query = query.query,
                    page = page,
                    apiKey = apiKey,
                    pageSize = pageSize,
                    dataTypes = dataTypeFilters,
                )

            val entities = response.foods.map(mapper::foodDataCentralProductEntity)
            val pagingKeys = entities.map {
                FoodDataCentralPagingKeyEntity(
                    queryString = query.query,
                    dataTypes = dataTypes,
                    fdcId = it.fdcId,
                )
            }

            val changed = dao.insertProductsWithPagingKeys(entities, pagingKeys)
            onNewProduct(changed.map(mapper::foodDataCentralProduct).toSet())

            return MediatorResult.Success(response.foods.size < pageSize)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            logger.e("Error during loading data from OpenFoodFacts", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "FoodDataCentralRemoteMediator"
    }
}
