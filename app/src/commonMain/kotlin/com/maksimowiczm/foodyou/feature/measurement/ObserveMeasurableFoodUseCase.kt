package com.maksimowiczm.foodyou.feature.measurement

import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.model.Food
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

data class MeasurableFood(
    val food: Food,
    val suggestions: List<Measurement>,
    val selected: Measurement? = null
)

interface ObserveMeasurableFoodUseCase {
    operator fun invoke(foodId: FoodId): Flow<MeasurableFood>
    operator fun invoke(measurementId: MeasurementId): Flow<MeasurableFood>
}

internal class ObserveMeasurableFoodUseCaseImpl(
    private val foodRepository: FoodRepository,
    private val measurementRepository: MeasurementRepository
) : ObserveMeasurableFoodUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(foodId: FoodId): Flow<MeasurableFood> =
        foodRepository.observeFood(foodId).filterNotNull().flatMapLatest { food ->
            measurementRepository
                .observeSuggestions(foodId)
                .map { suggestions ->
                    MeasurableFood(
                        food = food,
                        suggestions = suggestions
                    )
                }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(measurementId: MeasurementId) = measurementRepository
        .observeMeasurement(measurementId)
        .filterNotNull()
        .flatMapLatest { foodWithMeasurement ->
            val food = foodWithMeasurement.food
            val measurement = foodWithMeasurement.measurement

            measurementRepository
                .observeSuggestions(food.id)
                .map { list ->
                    // Replace the first suggestion with the current measurement
                    var replaced = false

                    val realSuggestions = list.map {
                        if (replaced) {
                            return@map it
                        }

                        val real = when (it) {
                            is Measurement.Gram -> measurement as? Measurement.Gram ?: return@map it
                            is Measurement.Package ->
                                measurement as? Measurement.Package
                                    ?: return@map it

                            is Measurement.Serving ->
                                measurement as? Measurement.Serving
                                    ?: return@map it
                        }

                        replaced = true
                        real
                    }

                    MeasurableFood(
                        food = food,
                        suggestions = realSuggestions,
                        selected = measurement
                    )
                }
        }
}
