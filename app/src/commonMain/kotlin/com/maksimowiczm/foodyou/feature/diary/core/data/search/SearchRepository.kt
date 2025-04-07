package com.maksimowiczm.foodyou.feature.diary.core.data.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.map
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Food
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import com.maksimowiczm.foodyou.feature.diary.core.data.food.NutrientValue.Companion.toNutrientValue
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Nutrients
import com.maksimowiczm.foodyou.feature.diary.core.data.food.PortionWeight
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Product
import com.maksimowiczm.foodyou.feature.diary.core.database.DiaryDatabase
import com.maksimowiczm.foodyou.feature.diary.core.database.search.FoodSearchVirtualEntity
import com.maksimowiczm.foodyou.feature.diary.core.database.search.SearchDao
import com.maksimowiczm.foodyou.feature.diary.core.database.search.SearchQueryEntity
import com.maksimowiczm.foodyou.feature.diary.core.network.ProductRemoteMediatorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface SearchRepository {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>>

    fun queryFood(query: String?): Flow<PagingData<Food>>
}

internal class SearchRepositoryImpl(
    database: DiaryDatabase,
    private val remoteMediatorFactory: ProductRemoteMediatorFactory,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SearchRepository {
    private val searchDao: SearchDao = database.searchDao
    private val ioScope = CoroutineScope(ioDispatcher + SupervisorJob())

    override fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>> =
        searchDao.observeRecentQueries(limit).map { list ->
            list.map {
                val date = Instant
                    .fromEpochSeconds(it.epochSeconds)
                    .toLocalDateTime(TimeZone.currentSystemDefault())

                SearchQuery(
                    query = it.query,
                    date = date
                )
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun queryFood(query: String?): Flow<PagingData<Food>> {
        // Handle different query formats
        val (effectiveQuery, extractedBarcode) = when {
            query == null -> null to null
            query.all(Char::isDigit) -> null to query
            query.contains("openfoodfacts.org/product/") -> {
                // Extract barcode from product URL
                val regex = "openfoodfacts\\.org/product/(\\d+)".toRegex()
                val barcode = regex.find(query)?.groupValues?.getOrNull(1)
                null to barcode
            }

            query.contains("openfoodfacts.org/cgi/search.pl") -> {
                // Extract search terms from search URL
                val regex = "search_terms=([^&]+)".toRegex()
                val searchTerms = regex.find(query)?.groupValues?.getOrNull(1)?.replace("+", " ")
                searchTerms to null
            }

            else -> query to null
        }

        val barcode = extractedBarcode
        val searchQuery = effectiveQuery ?: query
        val localOnly = query == null
        val remoteMediator: RemoteMediator<Int, FoodSearchVirtualEntity>? = when {
            localOnly -> null
            barcode != null -> remoteMediatorFactory.createWithBarcode(barcode)
            else -> remoteMediatorFactory.createWithQuery(searchQuery)
        }

        // Insert query if it's not a barcode and not empty
        if (barcode == null && searchQuery?.isNotBlank() == true) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(searchQuery)
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = 30
            ),
            remoteMediator = remoteMediator
        ) {
            if (barcode != null) {
                searchDao.queryFoodByBarcode(barcode)
            } else {
                val queryList = searchQuery
                    ?.split(" ")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.take(5)
                    ?: emptyList()

                val (query1, query2, query3, query4, query5) = queryList + List(5) { null }

                searchDao.queryFoodByText(
                    query1 = query1,
                    query2 = query2,
                    query3 = query3,
                    query4 = query4,
                    query5 = query5
                )
            }
        }.flow.map { data ->
            data.map { it.toFood() }
        }
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

        searchDao.upsert(
            SearchQueryEntity(
                query = query,
                epochSeconds = epochSeconds
            )
        )
    }
}

private fun FoodSearchVirtualEntity.toFood(): Food = Product(
    id = FoodId.Product(productId),
    name = name,
    brand = brand,
    barcode = null,
    nutrients = Nutrients(
        calories = nutrients.calories.toNutrientValue(),
        proteins = nutrients.proteins.toNutrientValue(),
        carbohydrates = nutrients.carbohydrates.toNutrientValue(),
        sugars = nutrients.sugars.toNutrientValue(),
        fats = nutrients.fats.toNutrientValue(),
        saturatedFats = nutrients.saturatedFats.toNutrientValue(),
        salt = nutrients.salt.toNutrientValue(),
        sodium = nutrients.sodium.toNutrientValue(),
        fiber = nutrients.fiber.toNutrientValue()
    ),
    packageWeight = packageWeight?.let { PortionWeight.Package(it) },
    servingWeight = servingWeight?.let { PortionWeight.Serving(it) }
)
