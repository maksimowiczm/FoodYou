package com.maksimowiczm.foodyou.feature.search.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.search.database.dao.OpenFoodFactsDao
import com.maksimowiczm.foodyou.feature.search.database.dao.ProductDao
import com.maksimowiczm.foodyou.feature.search.database.entity.FoodForm
import com.maksimowiczm.foodyou.feature.search.database.entity.Nutrients
import com.maksimowiczm.foodyou.feature.search.database.entity.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductEntity
import com.maksimowiczm.foodyou.feature.search.database.entity.ProductSource
import com.maksimowiczm.foodyou.feature.search.network.model.OpenFoodFactsProduct

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator(
    private val isBarcode: Boolean,
    private val query: String,
    private val country: String,
    private val openFoodFactsDao: OpenFoodFactsDao,
    private val productDao: ProductDao,
    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource
) : ProductRemoteMediator() {
    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
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
                    val product = openFoodFactsNetworkDataSource.getProduct(
                        code = query,
                        country = country
                    )?.toEntity()

                    if (product != null) {
                        productDao.upsertUniqueProducts(listOf(product))
                    }

                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val pagingKey = openFoodFactsDao.getPagingKey(
                        query = query,
                        country = country
                    )

                    if (pagingKey != null && pagingKey.totalCount == pagingKey.fetchedCount) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    val nextPage = (pagingKey?.fetchedCount?.div(PAGE_SIZE) ?: 0) + 1

                    nextPage
                }
            }

            Logger.d(TAG) { "Loading page $page" }

            val response = openFoodFactsNetworkDataSource.queryProducts(
                query = query,
                country = country,
                page = page,
                pageSize = PAGE_SIZE
            )

            val fetchedCount =
                ((response.page - 1) * response.pageSize).coerceAtLeast(0) + response.products.size

            openFoodFactsDao.upsertPagingKey(
                OpenFoodFactsPagingKeyEntity(
                    queryString = query,
                    country = country,
                    fetchedCount = fetchedCount,
                    totalCount = response.count
                )
            )

            val products = response.products.map { remoteProduct ->
                remoteProduct.toEntity().also {
                    if (it == null) {
                        Logger.w(TAG) {
                            "Failed to convert product: (name=${remoteProduct.productName}, code=${remoteProduct.code})"
                        }
                    }
                }
            }

            productDao.upsertUniqueProducts(products.filterNotNull())

            val skipped = products.count { it == null }

            val endOfPaginationReached = (products.size + skipped) < PAGE_SIZE

            return MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Failed to load page" }
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"

        // Feeling good about this page size might adjust later
        private const val PAGE_SIZE = 50
    }
}

private fun OpenFoodFactsProduct.toEntity(): ProductEntity? {
    val packageQuantityForm = packageQuantityUnit?.foodForm()
    val servingQuantityForm = servingQuantityUnit?.foodForm()

    if (
        packageQuantityForm != null &&
        servingQuantityForm != null &&
        packageQuantityForm != servingQuantityForm
    ) {
        return null
    }

    val form = packageQuantityForm ?: FoodForm.Solid

    if (listOf(
            nutrients.energy100g,
            nutrients.proteins100g,
            nutrients.carbohydrates100g,
            nutrients.fat100g,
            code
        ).any { it == null }
    ) {
        return null
    }

    return ProductEntity(
        name = productName,
        brand = brands,
        barcode = code,
        packageWeight = packageQuantity ?: 0f,
        servingWeight = servingQuantity ?: 0f,
        foodForm = form,
        productSource = ProductSource.OpenFoodFacts,
        nutrients = Nutrients(
            calories = nutrients.energy100g!!,
            proteins = nutrients.proteins100g!!,
            carbohydrates = nutrients.carbohydrates100g!!,
            sugars = nutrients.sugars100g,
            fats = nutrients.fat100g!!,
            saturatedFats = nutrients.saturatedFat100g,
            salt = nutrients.salt100g,
            sodium = nutrients.sodium100g,
            fiber = nutrients.fiber100g
        )
    )
}

private fun String.foodForm(): FoodForm? = when (this) {
    "g" -> FoodForm.Solid
    "ml" -> FoodForm.Liquid
    else -> null
}
