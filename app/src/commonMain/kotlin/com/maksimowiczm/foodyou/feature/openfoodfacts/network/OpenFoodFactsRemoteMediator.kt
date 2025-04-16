package com.maksimowiczm.foodyou.feature.openfoodfacts.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.data.database.openfoodfacts.OpenFoodFactsDao
import com.maksimowiczm.foodyou.core.data.database.product.ProductDao
import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.openfoodfacts.OpenFoodFactsPagingKeyEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductEntity
import com.maksimowiczm.foodyou.core.data.model.product.ProductSource
import com.maksimowiczm.foodyou.core.util.NutrientsHelper
import com.maksimowiczm.foodyou.feature.openfoodfacts.network.model.OpenFoodFactsNutrients
import com.maksimowiczm.foodyou.feature.openfoodfacts.network.model.OpenFoodFactsProduct

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator<T : Any>(
    private val isBarcode: Boolean,
    private val query: String,
    private val country: String?,
    private val openFoodFactsDao: OpenFoodFactsDao,
    private val productDao: ProductDao,
    private val openFoodFactsNetworkDataSource: OpenFoodFactsNetworkDataSource
) : RemoteMediator<Int, T>() {
    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, T>): MediatorResult {
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
                        productDao.insertOpenFoodFactsProducts(listOf(product))
                    }

                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val pagingKey = openFoodFactsDao.getPagingKey(
                        query = query,
                        country = country ?: "WORLD"
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
                    country = country ?: "WORLD",
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

            productDao.insertOpenFoodFactsProducts(products.filterNotNull())

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

/**
 * Converts an [OpenFoodFactsProduct] to a [ProductEntity]. Returns null if the conversion is not possible.
 */
private fun OpenFoodFactsProduct.toEntity(): ProductEntity? {
    val nutrients = nutrients ?: return null
    val productName = productName ?: return null

    val packageWeight = when {
        packageQuantityUnit != "g" && packageQuantityUnit != "ml" -> null
        else -> packageQuantity
    }

    val servingWeight = when {
        servingQuantityUnit != "g" && servingQuantityUnit != "ml" -> null
        else -> servingQuantity
    }

    if (
        packageQuantityUnit != null &&
        servingQuantityUnit != null &&
        packageQuantityUnit != servingQuantityUnit
    ) {
        return null
    }

    return ProductEntity(
        name = productName,
        brand = brands,
        barcode = code,
        nutrients = nutrients.toEntity() ?: return null,
        packageWeight = packageWeight,
        servingWeight = servingWeight,
        productSource = ProductSource.OpenFoodFacts
    )
}

private fun OpenFoodFactsNutrients.toEntity(): Nutrients? {
    if (
        proteins100g == null ||
        carbohydrates100g == null ||
        fat100g == null
    ) {
        return null
    }

    val energy100g = energy100g ?: NutrientsHelper.calculateCalories(
        proteins = proteins100g,
        carbohydrates = carbohydrates100g,
        fats = fat100g
    )

    return Nutrients(
        calories = energy100g,
        proteins = proteins100g,
        carbohydrates = carbohydrates100g,
        sugars = sugars100g,
        fats = fat100g,
        saturatedFats = saturatedFat100g,
        salt = salt100g,
        sodium = sodium100g,
        fiber = fiber100g
    )
}
