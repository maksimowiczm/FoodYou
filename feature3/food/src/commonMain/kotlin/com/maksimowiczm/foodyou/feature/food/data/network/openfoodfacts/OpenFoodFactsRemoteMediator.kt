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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalPagingApi::class)
internal class OpenFoodFactsRemoteMediator<T : Any>(
    private val remoteDataSource: OpenFoodFactsRemoteDataSource,
    foodDatabase: FoodDatabase,
    private val query: String,
    private val country: String?,
    private val isBarcode: Boolean
) : RemoteMediator<Int, T>() {

    private val productDao = foodDatabase.productDao
    private val openFoodFactsDao = foodDatabase.openFoodFactsDao

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    @OptIn(ExperimentalTime::class)
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
private fun NetworkOpenFoodFactsProduct.toEntity(): Product? {
    val name = name?.takeIf { it.isNotBlank() }
    if (name == null) {
        return null
    }

    if (packageQuantityUnit != null && packageQuantityUnit != "g" && packageQuantityUnit != "ml") {
        return null
    }

    if (servingQuantityUnit != null && servingQuantityUnit != "g" && servingQuantityUnit != "ml") {
        return null
    }

    val nutritionFacts = nutritionFacts
    if (nutritionFacts == null) {
        return null
    }

    return Product(
        name = name,
        brand = brand?.takeIf { it.isNotBlank() },
        barcode = barcode?.takeIf { it.isNotBlank() },
        packageWeight = packageWeight,
        servingWeight = servingWeight,
        nutrients = Nutrients(
            energy = nutritionFacts.energy?.toFloat(),
            proteins = nutritionFacts.proteins?.toFloat(),
            fats = nutritionFacts.fats?.toFloat(),
            saturatedFats = nutritionFacts.saturatedFats?.toFloat(),
            transFats = nutritionFacts.transFats?.toFloat(),
            monounsaturatedFats = nutritionFacts.monounsaturatedFats?.toFloat(),
            polyunsaturatedFats = nutritionFacts.polyunsaturatedFats?.toFloat(),
            omega3 = nutritionFacts.omega3Fats?.toFloat(),
            omega6 = nutritionFacts.omega6Fats?.toFloat(),
            carbohydrates = nutritionFacts.carbohydrates?.toFloat() ?: 0f,
            sugars = nutritionFacts.sugars?.toFloat(),
            addedSugars = nutritionFacts.addedSugars?.toFloat(),
            dietaryFiber = nutritionFacts.fiber?.toFloat(),
            solubleFiber = nutritionFacts.solubleFiber?.toFloat(),
            insolubleFiber = nutritionFacts.insolubleFiber?.toFloat(),
            salt = nutritionFacts.salt?.toFloat(),
            cholesterolMilli = nutritionFacts.cholesterol?.toFloat(),
            caffeineMilli = nutritionFacts.caffeine?.toFloat()
        ),
        vitamins = Vitamins(
            vitaminAMicro = nutritionFacts.vitaminA?.toFloat(),
            vitaminB1Milli = nutritionFacts.vitaminB1?.toFloat(),
            vitaminB2Milli = nutritionFacts.vitaminB2?.toFloat(),
            vitaminB3Milli = nutritionFacts.vitaminB3?.toFloat(),
            vitaminB5Milli = nutritionFacts.vitaminB5?.toFloat(),
            vitaminB6Milli = nutritionFacts.vitaminB6?.toFloat(),
            vitaminB7Micro = nutritionFacts.vitaminB7?.toFloat(),
            vitaminB9Micro = nutritionFacts.vitaminB9?.toFloat(),
            vitaminB12Micro = nutritionFacts.vitaminB12?.toFloat(),
            vitaminCMilli = nutritionFacts.vitaminC?.toFloat(),
            vitaminDMicro = nutritionFacts.vitaminD?.toFloat(),
            vitaminEMilli = nutritionFacts.vitaminE?.toFloat(),
            vitaminKMicro = nutritionFacts.vitaminK?.toFloat()
        ),
        minerals = Minerals(
            manganeseMilli = nutritionFacts.manganese?.toFloat(),
            magnesiumMilli = nutritionFacts.magnesium?.toFloat(),
            potassiumMilli = nutritionFacts.potassium?.toFloat(),
            calciumMilli = nutritionFacts.calcium?.toFloat(),
            copperMilli = nutritionFacts.copper?.toFloat(),
            zincMilli = nutritionFacts.zinc?.toFloat(),
            sodiumMilli = nutritionFacts.sodium?.toFloat(),
            ironMilli = nutritionFacts.iron?.toFloat(),
            phosphorusMilli = nutritionFacts.phosphorus?.toFloat(),
            seleniumMicro = nutritionFacts.selenium?.toFloat(),
            iodineMicro = nutritionFacts.iodine?.toFloat(),
            chromiumMicro = nutritionFacts.chromium?.toFloat()
        ),
        note = null,
        sourceType = FoodSource.Type.OpenFoodFacts,
        sourceUrl = url
    )
}
