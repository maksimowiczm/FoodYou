package com.maksimowiczm.foodyou.feature.addfood.data

import com.maksimowiczm.foodyou.core.database.measurement.MeasurementLocalDataSource
import com.maksimowiczm.foodyou.core.database.search.FoodSearchEntity
import com.maksimowiczm.foodyou.core.database.search.FoodSearchLocalDataSource
import com.maksimowiczm.foodyou.core.database.search.SearchLocalDataSource
import com.maksimowiczm.foodyou.core.database.search.SearchQueryEntity
import com.maksimowiczm.foodyou.core.domain.FoodRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementMapper
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalTime::class)
internal class AddFoodRepository(
    private val foodSearchLocalDataSource: FoodSearchLocalDataSource,
    private val measurementLocalDataSource: MeasurementLocalDataSource,
    private val searchLocalDataSource: SearchLocalDataSource,
    private val measurementMapper: MeasurementMapper,
    private val foodRepository: FoodRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    private val bgScope = CoroutineScope(dispatcher + SupervisorJob())

    @OptIn(ExperimentalCoroutinesApi::class)
    fun queryFood(query: String?, mealId: Long, date: LocalDate): Flow<List<SearchFoodItem>> {
        val barcode = query?.takeIf { it.all { it.isDigit() } }

        // Insert query if it's not a barcode and not empty
        if (barcode == null && query?.isNotBlank() == true) {
            bgScope.launch {
                insertProductQueryWithCurrentTime(query)
            }
        }

        val queryFlow = if (barcode != null) {
            foodSearchLocalDataSource.queryFoodByBarcode(barcode = barcode, limit = 100)
        } else {
            foodSearchLocalDataSource.queryFood(query = query, limit = 100)
        }

        val epoch = date.toEpochDays()

        return queryFlow.flatMapLatest { searchList ->
            if (searchList.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            val itemFlows = searchList.mapNotNull { entity ->
                return@mapNotNull when {
                    entity.productId != null -> mapToProduct(
                        entity = entity,
                        mealId = mealId,
                        epoch = epoch
                    )

                    entity.recipeId != null -> mapToRecipe(
                        entity = entity,
                        mealId = mealId,
                        epoch = epoch
                    )

                    else -> null
                }
            }

            combine(itemFlows) { it.toList() }.map { it.flatten() }
        }
    }

    private fun mapToProduct(
        entity: FoodSearchEntity,
        mealId: Long,
        epoch: Long
    ): Flow<List<SearchFoodItem>>? = entity.productId?.let { productId ->
        val measurementsFlow = measurementLocalDataSource.observeMeasurementsByProductMealDay(
            productId = productId,
            mealId = mealId,
            epochDay = epoch
        )

        val suggestionFlow = measurementLocalDataSource.observeAllMeasurementsByType(
            productId = productId
        ).map { it.firstOrNull() }

        val productFlow = foodRepository.observeFood(FoodId.Product(productId)).filterNotNull()

        val combinedFlow = combine(
            measurementsFlow,
            productFlow,
            suggestionFlow
        ) { measurements, product, suggestion ->
            val measurement = suggestion
                ?.let { measurementMapper.toMeasurement(suggestion) }
                ?: Measurement.defaultForFood(product)

            if (measurements.isEmpty()) {
                listOf(
                    SearchFoodItem(
                        food = product,
                        measurement = measurement,
                        measurementId = null,
                        uniqueId = "${product.id}-0"
                    )
                )
            } else {
                measurements.mapIndexed { i, measurementEntity ->
                    SearchFoodItem(
                        food = product,
                        measurement = measurementMapper.toMeasurement(measurementEntity),
                        measurementId = measurementEntity.id,
                        uniqueId = "${product.id}-$i"
                    )
                }
            }
        }

        combinedFlow
    }

    private fun mapToRecipe(
        entity: FoodSearchEntity,
        mealId: Long,
        epoch: Long
    ): Flow<List<SearchFoodItem>>? = entity.recipeId?.let { recipeId ->
        val measurementsFlow = measurementLocalDataSource.observeMeasurementsByRecipeMealDay(
            recipeId = recipeId,
            mealId = mealId,
            epochDay = epoch
        )

        val suggestionFlow = measurementLocalDataSource.observeAllMeasurementsByType(
            recipeId = recipeId
        ).map { it.firstOrNull() }

        val recipeFlow = foodRepository.observeFood(FoodId.Recipe(recipeId)).filterNotNull()

        val combinedFlow = combine(
            measurementsFlow,
            recipeFlow,
            suggestionFlow
        ) { measurements, recipe, suggestion ->
            val measurement = suggestion
                ?.let { measurementMapper.toMeasurement(suggestion) }
                ?: Measurement.defaultForFood(recipe)

            if (measurements.isEmpty()) {
                listOf(
                    SearchFoodItem(
                        food = recipe,
                        measurement = measurement,
                        measurementId = null,
                        uniqueId = "${recipe.id}-0"
                    )
                )
            } else {
                measurements.mapIndexed { i, measurementEntity ->
                    SearchFoodItem(
                        food = recipe,
                        measurement = measurementMapper.toMeasurement(measurementEntity),
                        measurementId = measurementEntity.id,
                        uniqueId = "${recipe.id}-$i"
                    )
                }
            }
        }

        combinedFlow
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
