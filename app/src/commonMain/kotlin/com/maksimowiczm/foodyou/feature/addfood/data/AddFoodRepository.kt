package com.maksimowiczm.foodyou.feature.addfood.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.map
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.measurement.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.core.database.search.FoodSearchVirtualEntity
import com.maksimowiczm.foodyou.core.database.search.SearchQueryEntity
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.model.PortionWeight
import com.maksimowiczm.foodyou.core.repository.ProductRemoteMediatorFactory
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

internal class AddFoodRepository(
    database: FoodYouDatabase,
    private val remoteMediatorFactory: ProductRemoteMediatorFactory,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    val searchDao = database.searchDao
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
                searchDao.queryFoodByBarcode(
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

                searchDao.queryFood(
                    query1 = query1,
                    query2 = query2,
                    query3 = query3,
                    query4 = query4,
                    query5 = query5,
                    mealId = mealId,
                    epochDay = date.toEpochDays()
                )
            }
        }.flow.map { data ->
            data.map { it.toSearchFoodItem() }
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

private fun FoodSearchVirtualEntity.toSearchFoodItem(): SearchFoodItem {
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
                measurement = toMeasurement(),
                uniqueId = foodId.uniqueId(measurementId)
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
                measurement = toMeasurement(),
                uniqueId = foodId.uniqueId(measurementId)
            )
        }

        else -> error("Data inconsistency: productId and recipeId are null")
    }
}

private fun FoodSearchVirtualEntity.toMeasurement(): Measurement = when (measurement) {
    MeasurementEntity.Gram -> Measurement.Gram(quantity)
    MeasurementEntity.Package -> Measurement.Package(quantity)
    MeasurementEntity.Serving -> Measurement.Serving(quantity)
}

private fun FoodId.uniqueId(measurementId: MeasurementId?): String {
    val measurementId = measurementId?.let {
        when (it) {
            is MeasurementId.Product -> it.id
            is MeasurementId.Recipe -> it.id
        }
    }

    return when (this) {
        is FoodId.Product if (measurementId != null) -> "p_${id}_$measurementId"
        is FoodId.Product -> "p_$id"
        is FoodId.Recipe if (measurementId != null) -> "r_${id}_$measurementId"
        is FoodId.Recipe -> "r_$id"
    }
}
