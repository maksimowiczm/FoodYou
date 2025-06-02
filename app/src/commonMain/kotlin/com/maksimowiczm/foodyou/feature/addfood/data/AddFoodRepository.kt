package com.maksimowiczm.foodyou.feature.addfood.data

import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate

internal class AddFoodRepository(dispatcher: CoroutineDispatcher = Dispatchers.Default) {
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

//        val queryFlow = if (barcode != null) {
//            foodLocalDataSource.queryFoodByBarcode(barcode)
//        } else {
//            foodLocalDataSource.queryFood(query)
//        }
//
//        val epoch = date.toEpochDays()
//
//        return queryFlow.flatMapLatest { searchList ->
//            if (searchList.isEmpty()) {
//                return@flatMapLatest flowOf(emptyList())
//            }
//
//            val itemFlows = searchList.mapNotNull { entity ->
//                return@mapNotNull when {
//                    entity.productId != null -> mapToProduct(entity, mealId, epoch)
//                    entity.recipeId != null -> mapToRecipe(entity, mealId, epoch)
//                    else -> null
//                }
//            }
//
//            combine(itemFlows) { it.toList() }.map { it.flatten() }
//        }

        TODO()
    }

//    private fun mapToProduct(
//        entity: FoodSearchEntity,
//        mealId: Long,
//        epoch: Int
//    ): Flow<List<SearchFoodItem>>? = entity.productId?.let { productId ->
// //        val measurementsFlow = productMeasurementDao.observeMeasurementsByProductMealDay(
// //            productId = productId,
// //            mealId = mealId,
// //            epochDay = epoch
// //        )
// //
// //        val suggestionFlow = productMeasurementDao.observeLatestProductMeasurementSuggestion(
// //            productId = productId
// //        )
// //
// //        val productFlow = foodRepository.observeFood(FoodId.Product(productId)).filterNotNull()
// //
// //        val combinedFlow = combine(
// //            measurementsFlow,
// //            productFlow,
// //            suggestionFlow
// //        ) { measurements, product, suggestion ->
// //            val measurement = suggestion
// //                ?.let { measurementMapper.toMeasurement(suggestion) }
// //                ?: Measurement.defaultForFood(product)
// //
// //            if (measurements.isEmpty()) {
// //                listOf(
// //                    SearchFoodItem(
// //                        food = product,
// //                        measurement = measurement,
// //                        measurementId = null,
// //                        uniqueId = "${product.id}-0"
// //                    )
// //                )
// //            } else {
// //                measurements.mapIndexed { i, measurementEntity ->
// //                    SearchFoodItem(
// //                        food = product,
// //                        measurement = measurementMapper.toMeasurement(
// //                            measurementEntity.measurement
// //                        ),
// //                        measurementId = MeasurementId.Product(
// //                            measurementEntity.measurement.id
// //                        ),
// //                        uniqueId = "${product.id}-$i"
// //                    )
// //                }
// //            }
// //        }
// //
// //        combinedFlow
//
//        TODO()
//    }

//    private fun mapToRecipe(
//        entity: FoodSearchEntity,
//        mealId: Long,
//        epoch: Int
//    ): Flow<List<SearchFoodItem>>? = entity.recipeId?.let { recipeId ->
// //        val measurementsFlow = recipeMeasurementDao.observeMeasurementsByRecipeMealDay(
// //            recipeId = recipeId,
// //            mealId = mealId,
// //            epochDay = epoch
// //        )
// //
// //        val suggestionFlow = recipeMeasurementDao.observeLatestRecipeMeasurementSuggestion(
// //            recipeId = recipeId
// //        )
// //
// //        val recipeFlow = foodRepository.observeFood(FoodId.Recipe(recipeId)).filterNotNull()
// //
// //        val combinedFlow = combine(
// //            measurementsFlow,
// //            recipeFlow,
// //            suggestionFlow
// //        ) { measurements, recipe, suggestion ->
// //            val measurement = suggestion
// //                ?.let { measurementMapper.toMeasurement(suggestion) }
// //                ?: Measurement.defaultForFood(recipe)
// //
// //            if (measurements.isEmpty()) {
// //                listOf(
// //                    SearchFoodItem(
// //                        food = recipe,
// //                        measurement = measurement,
// //                        measurementId = null,
// //                        uniqueId = "${recipe.id}-0"
// //                    )
// //                )
// //            } else {
// //                measurements.mapIndexed { i, measurementEntity ->
// //                    SearchFoodItem(
// //                        food = recipe,
// //                        measurement = measurementMapper.toMeasurement(measurementEntity),
// //                        measurementId = MeasurementId.Recipe(measurementEntity.id),
// //                        uniqueId = "${recipe.id}-$i"
// //                    )
// //                }
// //            }
// //        }
// //
// //        combinedFlow
//        TODO()
//    }

    private suspend fun insertProductQueryWithCurrentTime(query: String) {
        val epochSeconds = Clock.System.now().epochSeconds

//        searchLocalDataSource.upsert(
//            SearchQueryEntity(
//                query = query,
//                epochSeconds = epochSeconds
//            )
//        )
        TODO()
    }
}
