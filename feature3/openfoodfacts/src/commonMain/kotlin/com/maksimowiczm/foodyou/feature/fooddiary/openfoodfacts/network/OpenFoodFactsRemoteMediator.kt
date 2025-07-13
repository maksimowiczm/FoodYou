package com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.data.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct as NetworkOpenFoodFactsProduct
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator(
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    private val dao: OpenFoodFactsDao,
    private val query: String,
    private val country: String?,
    private val isBarcode: Boolean
) : RemoteMediator<Int, OpenFoodFactsProduct>() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    @OptIn(ExperimentalTime::class)
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, OpenFoodFactsProduct>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    // Currently there is no way to refresh the data other than delete all and fetch again.
                    return MediatorResult.Success(endOfPaginationReached = false)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                // Handle barcode search as a special case.
                LoadType.APPEND if (isBarcode) -> {
                    val response = remoteDataSource.getProduct(
                        barcode = query,
                        countries = country
                    ).getOrElse {
                        return if (it is ProductNotFoundException) {
                            MediatorResult.Success(endOfPaginationReached = true)
                        } else {
                            MediatorResult.Error(it)
                        }
                    }

                    val product = response.toEntity()

                    if (product != null) {
                        dao.insertUniqueProduct(product)
                    }
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val pagingKey = dao.getPagingKey(
                        query = query,
                        country = country ?: "world"
                    )

                    if (pagingKey != null && pagingKey.totalCount == pagingKey.fetchedCount) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val nextPage = (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1

                    nextPage
                }
            }

            Logger.d(TAG) { "Loading page $page" }

            val response = remoteDataSource.queryProducts(
                query = query,
                countries = country,
                page = page,
                pageSize = PAGE_SIZE
            )

            val fetchedCount =
                ((response.page - 1) * response.pageSize).coerceAtLeast(0) + response.products.size

            dao.upsertPagingKey(
                OpenFoodFactsPagingKey(
                    queryString = query,
                    country = country ?: "world",
                    fetchedCount = fetchedCount,
                    totalCount = response.count
                )
            )

            val timestamp = Clock.System.now()
            val products = response.products.map { remoteProduct ->
                remoteProduct.toEntity(timestamp).also {
                    if (it == null) {
                        Logger.w(TAG) {
                            "Failed to convert product: (name=${remoteProduct.name}, code=${remoteProduct.barcode})"
                        }
                    }
                }
            }

            dao.insertUniqueProducts(products.filterNotNull())

            val skipped = products.count { it == null }
            val endOfPaginationReached = (products.size + skipped) < PAGE_SIZE

            return MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Error loading page" }
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"

        // Feeling good about this page size might adjust later
        private const val PAGE_SIZE = 50
    }
}

@OptIn(ExperimentalTime::class)
private fun NetworkOpenFoodFactsProduct.toEntity(
    timestamp: Instant = Clock.System.now()
): OpenFoodFactsProduct? {
    val name = name
    if (name == null) {
        return null
    }

    if (packageQuantityUnit != null && packageQuantityUnit != "g" && packageQuantityUnit != "ml") {
        return null
    }

    if (servingQuantityUnit != null && servingQuantityUnit != "g" && servingQuantityUnit != "ml") {
        return null
    }

    return OpenFoodFactsProduct(
        name = name,
        brand = brand,
        barcode = barcode,
        packageWeight = packageWeight,
        servingWeight = servingWeight,
        nutritionFacts = nutritionFacts,
        downloadedAtEpochSeconds = timestamp.epochSeconds
    )
}
