package com.maksimowiczm.foodyou.feature.diary.addfood.measurement.domain

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.feature.diary.core.data.food.Food
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodId
import com.maksimowiczm.foodyou.feature.diary.core.data.food.FoodRepository
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.Measurement
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@Immutable
internal data class MeasurableFood(
    val food: Food,
    val suggestions: List<Measurement>,
    val selected: Measurement? = null
)

internal interface ObserveMeasurableFoodUseCase {
    operator fun invoke(foodId: FoodId): Flow<MeasurableFood>
    operator fun invoke(measurementId: MeasurementId): Flow<MeasurableFood>
}

internal class ObserveMeasurableFoodUseCaseImpl(
    private val foodRepository: FoodRepository,
    private val measurementRepository: MeasurementRepository
) : ObserveMeasurableFoodUseCase {
    override fun invoke(foodId: FoodId): Flow<MeasurableFood> =
        foodRepository.observeFood(foodId).filterNotNull().map {
            val suggestions = measurementRepository.getSuggestions(foodId)

            MeasurableFood(
                food = it,
                suggestions = suggestions
            )
        }

    override fun invoke(measurementId: MeasurementId) = measurementRepository
        .observeMeasurement(measurementId)
        .filterNotNull()
        .map {
            val food = it.food
            val measurement = it.measurement

            // Replace the first suggestion with the current measurement
            var replaced = false
            val suggestions = measurementRepository.getSuggestions(food.id)
            val realSuggestions = suggestions.map {
                if (replaced) {
                    return@map it
                }

                val real = when (it) {
                    is Measurement.Gram -> measurement as? Measurement.Gram ?: return@map it
                    is Measurement.Package -> measurement as? Measurement.Package ?: return@map it
                    is Measurement.Serving -> measurement as? Measurement.Serving ?: return@map it
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
