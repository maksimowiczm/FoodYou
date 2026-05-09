package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.extension.immediateTransaction
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsUrlSearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsV2RemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.SearchaliciousRemoteDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsDatabase
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductEntity
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator(
    private val query: SearchQuery.NotBlank,
    private val database: OpenFoodFactsDatabase,
    private val search: SearchaliciousRemoteDataSource,
    private val apiV2: OpenFoodFactsV2RemoteDataSource,
    private val mapper: OpenFoodFactsProductMapper,
    private val pageSize: Int,
    logger: Logger,
) : RemoteMediator<Int, OpenFoodFactsProductEntity>() {
    private val logger = logger.withTag(TAG)
    private val dao = database.dao

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
                            is OpenFoodFactsUrlSearchQuery -> {
                                val barcode =
                                    when (query) {
                                        is SearchQuery.Barcode -> query.barcode
                                        is OpenFoodFactsUrlSearchQuery -> query.barcode
                                        else -> error("Unreachable")
                                    }

                                val existingProduct = dao.observeCountByBarcode(barcode).first()
                                if (existingProduct > 0) {
                                    return MediatorResult.Success(endOfPaginationReached = true)
                                }

                                val response =
                                    apiV2.getProduct(barcode).getOrElse {
                                        return if (it is OpenFoodFactsApiError.ProductNotFound)
                                            MediatorResult.Success(endOfPaginationReached = true)
                                        else MediatorResult.Error(it)
                                    }

                                val product = mapper.toEntity(response)
                                dao.upsertProduct(product)
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }

                            is SearchQuery.NotBlank -> {
                                val count = dao.getPagingKeyCountByQuery(query.query)
                                val nextPage = (count / pageSize) + 1
                                nextPage
                            }
                        }
                }

            logger.d { "Loading page $page" }

            val response = search.search(query = query.query, page = page, pageSize = pageSize)

            val entities = response.hits.map(mapper::toEntity)
            val pagingKeys = entities.map {
                OpenFoodFactsPagingKeyEntity(queryString = query.query, productBarcode = it.barcode)
            }

            database.immediateTransaction {
                dao.upsertProducts(entities)
                dao.insertPagingKeys(pagingKeys)
            }

            return MediatorResult.Success(response.hits.size < response.pageSize)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            logger.e("Error during loading data from OpenFoodFacts", e)
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        const val TAG = "OpenFoodFactsRemoteMediator"
    }
}
