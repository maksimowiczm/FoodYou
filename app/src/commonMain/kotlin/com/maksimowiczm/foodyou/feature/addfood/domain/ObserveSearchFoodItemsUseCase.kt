package com.maksimowiczm.foodyou.feature.addfood.domain

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.flatMap
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.MeasurementId
import com.maksimowiczm.foodyou.core.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.repository.SearchRepository
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

    val weight: Float?
        get() = measurement.weight(food)
}

internal fun interface ObserveSearchFoodUseCase {
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
            val measurement = measurementRepository.getSuggestion(food.id)

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
