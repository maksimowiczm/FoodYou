package com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.Minerals
import com.maksimowiczm.foodyou.feature.food.data.database.food.Nutrients
import com.maksimowiczm.foodyou.feature.food.data.database.food.Product
import com.maksimowiczm.foodyou.feature.food.data.database.food.Vitamins
import com.maksimowiczm.foodyou.feature.food.data.database.openfoodfacts.OpenFoodFactsPagingKey
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.ProductNotFoundException
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.model.OpenFoodFactsProduct as NetworkOpenFoodFactsProduct

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator<T : Any>(
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    foodDatabase: FoodDatabase,
    private val query: String,
    private val country: String?,
    private val isBarcode: Boolean,
    mapper: OpenFoodFactsProductMapper
) : RemoteMediator<Int, T>() {

    private val productDao = foodDatabase.productDao
    private val openFoodFactsDao = foodDatabase.openFoodFactsDao

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
                        productDao.insertUniqueProduct(product)
                    }
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val pagingKey = openFoodFactsDao.getPagingKey(
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

            openFoodFactsDao.upsertPagingKey(
                OpenFoodFactsPagingKey(
                    queryString = query,
                    country = country ?: "world",
                    fetchedCount = fetchedCount,
                    totalCount = response.count
                )
            )

            val products = response.products.map { remoteProduct ->
                remoteProduct.toEntity().also {
                    if (it == null) {
                        Logger.w(TAG) {
                            "Failed to convert product: (name=${remoteProduct.name}, code=${remoteProduct.barcode})"
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
            return MediatorResult.Error(e)
        }
    }

    private companion object {
        private const val TAG = "OpenFoodFactsRemoteMediator"

        // Feeling good about this page size might adjust later
        private const val PAGE_SIZE = 50
    }

    private val m = mapper
    private fun NetworkOpenFoodFactsProduct.toEntity(): Product? = m.toRemoteProduct(this).run {
        // Ignore products with no name or nutrition facts
        if (name == null || nutritionFacts == null) {
            return null
        }

        return Product(
            name = name,
            brand = brand?.takeIf { it.isNotBlank() },
            barcode = barcode?.takeIf { it.isNotBlank() },
            packageWeight = packageWeight,
            servingWeight = servingWeight,
            nutrients = Nutrients(
                energy = nutritionFacts.energy,
                proteins = nutritionFacts.proteins,
                fats = nutritionFacts.fats,
                saturatedFats = nutritionFacts.saturatedFats,
                transFats = nutritionFacts.transFats,
                monounsaturatedFats = nutritionFacts.monounsaturatedFats,
                polyunsaturatedFats = nutritionFacts.polyunsaturatedFats,
                omega3 = nutritionFacts.omega3,
                omega6 = nutritionFacts.omega6,
                carbohydrates = nutritionFacts.carbohydrates ?: 0f,
                sugars = nutritionFacts.sugars,
                addedSugars = nutritionFacts.addedSugars,
                dietaryFiber = nutritionFacts.fiber,
                solubleFiber = nutritionFacts.solubleFiber,
                insolubleFiber = nutritionFacts.insolubleFiber,
                salt = nutritionFacts.salt,
                cholesterolMilli = nutritionFacts.cholesterolMilli,
                caffeineMilli = nutritionFacts.caffeineMilli
            ),
            vitamins = Vitamins(
                vitaminAMicro = nutritionFacts.vitaminAMicro,
                vitaminB1Milli = nutritionFacts.vitaminB1Milli,
                vitaminB2Milli = nutritionFacts.vitaminB2Milli,
                vitaminB3Milli = nutritionFacts.vitaminB3Milli,
                vitaminB5Milli = nutritionFacts.vitaminB5Milli,
                vitaminB6Milli = nutritionFacts.vitaminB6Milli,
                vitaminB7Micro = nutritionFacts.vitaminB7Micro,
                vitaminB9Micro = nutritionFacts.vitaminB9Micro,
                vitaminB12Micro = nutritionFacts.vitaminB12Micro,
                vitaminCMilli = nutritionFacts.vitaminCMilli,
                vitaminDMicro = nutritionFacts.vitaminDMicro,
                vitaminEMilli = nutritionFacts.vitaminEMilli,
                vitaminKMicro = nutritionFacts.vitaminKMicro
            ),
            minerals = Minerals(
                manganeseMilli = nutritionFacts.manganeseMilli,
                magnesiumMilli = nutritionFacts.magnesiumMilli,
                potassiumMilli = nutritionFacts.potassiumMilli,
                calciumMilli = nutritionFacts.calciumMilli,
                copperMilli = nutritionFacts.copperMilli,
                zincMilli = nutritionFacts.zincMilli,
                sodiumMilli = nutritionFacts.sodiumMilli,
                ironMilli = nutritionFacts.ironMilli,
                phosphorusMilli = nutritionFacts.phosphorusMilli,
                seleniumMicro = nutritionFacts.seleniumMicro,
                iodineMicro = nutritionFacts.iodineMicro,
                chromiumMicro = nutritionFacts.chromiumMicro
            ),
            note = null,
            sourceType = FoodSource.Type.OpenFoodFacts,
            sourceUrl = this@toEntity.url
        )
    }
}
