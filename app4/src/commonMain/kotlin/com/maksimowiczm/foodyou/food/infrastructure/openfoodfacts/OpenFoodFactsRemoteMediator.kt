package com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.room.immediateTransaction
import com.maksimowiczm.foodyou.food.domain.FoodDatabaseError
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.food.infrastructure.openfoodfacts.room.OpenFoodFactsProductEntity
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.ensureActive

// TODO
//  Fix issue with downloading first pages multiple times
@OptIn(ExperimentalPagingApi::class)
class OpenFoodFactsRemoteMediator(
    private val query: SearchQuery.NotBlank,
    private val database: OpenFoodFactsDatabase,
    private val remote: OpenFoodFactsRemoteDataSource,
    logger: Logger,
) : RemoteMediator<Int, OpenFoodFactsProductEntity>() {
    private val logger = logger.withTag(TAG)
    private val dao = database.dao
    private val mapper = OpenFoodFactsProductMapper()

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OpenFoodFactsProductEntity>,
    ): MediatorResult {
        try {
            val page =
                when (loadType) {
                    LoadType.REFRESH ->
                        return MediatorResult.Success(endOfPaginationReached = false)

                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

                    LoadType.APPEND ->
                        when (query) {
                            is SearchQuery.Barcode,
                            is SearchQuery.OpenFoodFactsUrl -> {
                                val barcode =
                                    when (query) {
                                        is SearchQuery.Barcode -> query.barcode
                                        is SearchQuery.OpenFoodFactsUrl -> query.barcode
                                        else -> error("Unreachable")
                                    }

                                val response =
                                    remote.getProduct(barcode).getOrElse {
                                        return if (it is FoodDatabaseError.ProductNotFound)
                                            MediatorResult.Success(endOfPaginationReached = true)
                                        else MediatorResult.Error(it)
                                    }

                                val product = mapper.openFoodFactsProductEntity(response)
                                dao.upsertProduct(product)
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }

                            is SearchQuery.Text -> {
                                val count = dao.getPagingKeyCountByQuery(query.query)
                                val nextPage = (count / PAGE_SIZE) + 1
                                nextPage
                            }
                        }
                }

            logger.d { "Loading page $page" }

            val response =
                remote.queryProducts(query = query.query, page = page, pageSize = PAGE_SIZE)

            val entities = response.products.map(mapper::openFoodFactsProductEntity)
            val pagingKeys =
                entities.map {
                    OpenFoodFactsPagingKeyEntity(
                        queryString = query.query,
                        productBarcode = it.barcode,
                    )
                }

            database.immediateTransaction {
                dao.upsertProducts(entities)
                dao.insertPagingKeys(pagingKeys)
            }

            val skipped = response.products.size - entities.size
            val endOfPaginationReached = entities.size + skipped < PAGE_SIZE

            return if (skipped == PAGE_SIZE) {
                logger.w { "All products on page were skipped" }
                load(loadType, state)
            } else {
                MediatorResult.Success(endOfPaginationReached)
            }
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            logger.e("Error during loading data from OpenFoodFacts", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        const val TAG = "OpenFoodFactsRemoteMediator"
        const val PAGE_SIZE = 50
    }
}
