package com.maksimowiczm.foodyou.feature.addfood.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.data.model.food.FoodSearchEntity
import com.maksimowiczm.foodyou.core.data.model.search.SearchQueryEntity
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.source.FoodLocalDataSource
import com.maksimowiczm.foodyou.core.domain.source.SearchLocalDataSource
import com.maksimowiczm.foodyou.core.ext.mapValues
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

internal class AddFoodRepository(
    private val searchLocalDataSource: SearchLocalDataSource,
    private val foodLocalDataSource: FoodLocalDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val ioScope = CoroutineScope(ioDispatcher + SupervisorJob())

    @OptIn(ExperimentalPagingApi::class)
    fun queryFood(query: String?, mealId: Long, date: LocalDate): Flow<PagingData<SearchFoodItem>> {
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

        // Insert query if it's not a barcode and not empty
        if (barcode == null && searchQuery?.isNotBlank() == true) {
            ioScope.launch {
                insertProductQueryWithCurrentTime(searchQuery)
            }
        }

        return Pager(
            config = PagingConfig(
                pageSize = 30
            )
        ) {
            if (barcode != null) {
                foodLocalDataSource.queryFoodByBarcode(
                    barcode = barcode,
                    mealId = mealId,
                    epochDay = date.toEpochDays()
                )
            } else {
                val queryList = searchQuery
                    ?.split(" ")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.take(5)
                    ?: emptyList()

                val (query1, query2, query3, query4, query5) = queryList + List(5) { null }

                foodLocalDataSource.queryFood(
                    query1 = query1,
                    query2 = query2,
                    query3 = query3,
                    query4 = query4,
                    query5 = query5,
                    mealId = mealId,
                    epochDay = date.toEpochDays()
                )
            }
        }.flow.mapValues { it.toSearchFoodItem() }
    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

        searchLocalDataSource.upsert(
            SearchQueryEntity(
                query = query,
                epochSeconds = epochSeconds
            )
        )
    }
}

private fun FoodSearchEntity.toSearchFoodItem(): SearchFoodItem {
    when {
        productId != null && recipeId == null -> {
            val foodId = FoodId.Product(productId)
            val measurementId = measurementId?.let { MeasurementId.Product(measurementId) }

            return SearchFoodItem(
                foodId = foodId,
                name = name,
                brand = brand,
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                packageWeight = packageWeight?.let { PortionWeight.Package(it) },
                servingWeight = servingWeight?.let { PortionWeight.Serving(it) },
                measurementId = measurementId,
                measurement = with(MeasurementMapper) { toMeasurement() }
            )
        }

        recipeId != null && productId == null -> {
            val foodId = FoodId.Recipe(recipeId)
            val measurementId = measurementId?.let { MeasurementId.Recipe(measurementId) }

            return SearchFoodItem(
                foodId = foodId,
                name = name,
                brand = brand,
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                packageWeight = packageWeight?.let { PortionWeight.Package(it) },
                servingWeight = servingWeight?.let { PortionWeight.Serving(it) },
                measurementId = measurementId,
                measurement = with(MeasurementMapper) { toMeasurement() }
            )
        }

        else -> error("Data inconsistency: productId and recipeId are null")
    }
}
