package com.maksimowiczm.foodyou.openfoodfacts.infrastructure

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsApiError
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsCredentials
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsUrlSearchQuery
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsApiV2ProductDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.network.OpenFoodFactsSearchDataSource
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsPagingKeyDao
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductDao
import com.maksimowiczm.foodyou.openfoodfacts.infrastructure.room.OpenFoodFactsProductEntity
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator(
    private val query: SearchQuery.NotBlank,
    private val credentials: OpenFoodFactsCredentials.Decrypted?,
    private val pagingKeyDao: OpenFoodFactsPagingKeyDao<*>,
    private val productDao: OpenFoodFactsProductDao,
    private val search: OpenFoodFactsSearchDataSource,
    private val apiV2: OpenFoodFactsApiV2ProductDataSource,
    private val mapper: OpenFoodFactsProductMapper,
    private val pageSize: Int,
    logger: Logger,
    private val onNewProduct: suspend (Set<OpenFoodFactsProduct>) -> Unit,
) : RemoteMediator<Int, OpenFoodFactsProductEntity>() {
    private val logger = logger.withTag(TAG)

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

                                val response =
                                    apiV2.getProduct(barcode).getOrElse {
                                        return if (it is OpenFoodFactsApiError.ProductNotFound)
                                            MediatorResult.Success(endOfPaginationReached = true)
                                        else MediatorResult.Error(it)
                                    }

                                val product = mapper.toEntity(response)
                                productDao.upsertProductAndGet(product)?.let {
                                    onNewProduct(setOf(mapper.toModel(it)))
                                }
                                return MediatorResult.Success(endOfPaginationReached = true)
                            }

                            is SearchQuery.NotBlank -> {
                                val count = pagingKeyDao.getPagingKeyCountByQuery(query.query)
                                val nextPage = (count / pageSize) + 1
                                nextPage
                            }
                        }
                }

            logger.d { "Loading page $page" }

            val response =
                search.search(
                    query = query.query,
                    credentials = credentials,
                    page = page,
                    pageSize = pageSize,
                )

            val entities = response.products.map(mapper::toEntity)

            val changed = pagingKeyDao.insertProductsWithPagingKeys(entities, query.query)
            onNewProduct(changed.map(mapper::toModel).toSet())

            return MediatorResult.Success(response.products.size < response.pageSize)
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
