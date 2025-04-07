package com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.flatMap
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Food
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Product
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.FoodWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.Measurement
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.search.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

@Immutable
internal data class SearchFoodItem(
    val food: Food,
    val measurement: Measurement,
    val measurementId: MeasurementId?,
    val uniqueId: String
) {
    val isSelected: Boolean
        get() = measurementId != null

    /**
     * Weight in grams. If weight is null then measurement is invalid. (e.g. 1 x package while
     * product has no package weight)
     */
    val weight: Float?
        get() = when (measurement) {
            is Measurement.Gram -> measurement.value
            is Measurement.Package -> when (food) {
                is Product -> food.packageWeight?.let { measurement.weight(food.packageWeight) }
            }

            is Measurement.Serving -> when (food) {
                is Product -> food.servingWeight?.let { measurement.weight(food.servingWeight) }
            }
        }
}

internal interface ObserveSearchFoodUseCase {
    suspend operator fun invoke(
        query: String?,
        mealId: Long,
        date: LocalDate,
        cache: CoroutineScope
    ): Flow<PagingData<SearchFoodItem>>
}

internal class ObserveSearchFoodUseCaseImpl(
    private val searchRepository: SearchRepository,
    private val measurementRepository: MeasurementRepository
) : ObserveSearchFoodUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun invoke(
        query: String?,
        mealId: Long,
        date: LocalDate,
        cache: CoroutineScope
    ): Flow<PagingData<SearchFoodItem>> = searchRepository
        .queryFood(
            query = query
        )
        .cachedIn(cache) // It has to be cached here to not collect it twice
        .flatMapLatest { pagingData ->
            measurementRepository.observeMeasurements(
                date = date,
                mealId = mealId
            ).map { measurements ->
                pagingData.flatMap { food ->
                    handle(measurements, food)
                }
            }
        }

    private suspend fun handle(
        measurements: List<FoodWithMeasurement>,
        food: Food
    ): List<SearchFoodItem> {
        val ids = measurements.filter { it.food.id == food.id }

        return if (ids.isEmpty()) {
            val measurement = measurementRepository.getSuggestion(food.id) ?: when (food) {
                is Product -> when {
                    food.servingWeight != null -> Measurement.Serving(1f)
                    food.packageWeight != null -> Measurement.Package(1f)
                    else -> Measurement.Gram(100f)
                }
            }

            listOf(
                SearchFoodItem(
                    food = food,
                    measurement = measurement,
                    measurementId = null,
                    uniqueId = food.id.uniqueId(null)
                )
            )
        } else {
            ids.map {
                SearchFoodItem(
                    food = food,
                    measurement = it.measurement,
                    measurementId = it.measurementId,
                    uniqueId = food.id.uniqueId(it.measurementId)
                )
            }
        }
    }
}

private fun FoodId.uniqueId(measurementId: MeasurementId?): String {
    val measurementId = measurementId?.let {
        when (it) {
            is MeasurementId.Product -> it.id
        }
    }

    return when (this) {
        is FoodId.Product if (measurementId != null) -> "p_${id}_$measurementId"
        is FoodId.Product -> "p_$id"
    }
}
