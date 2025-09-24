package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.common.domain.measurement.MeasurementType
import com.maksimowiczm.foodyou.food.domain.entity.Food
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveMeasurementSuggestionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

internal class MeasureIngredientViewModel(
    foodId: FoodId,
    observeFoodUseCase: ObserveFoodUseCase,
    observeMeasurementSuggestionsUseCase: ObserveMeasurementSuggestionsUseCase,
) : ViewModel() {
    val food: StateFlow<Food?> =
        observeFoodUseCase
            .observe(foodId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val possibleMeasurements: StateFlow<List<MeasurementType>?> =
        food
            .filterNotNull()
            .flatMapLatest { food -> food.possibleMeasurementTypes }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val suggestions: StateFlow<List<Measurement>?> =
        observeMeasurementSuggestionsUseCase
            .observe(foodId, limit = 10)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}

// These extensions will probably be moved into business when user would be able to choose between
// metric and imperial measurements. This is why they are wrapped in Flow, so they can be
// easily converted to the appropriate measurement system later.
private val Food.possibleMeasurementTypes: Flow<List<MeasurementType>>
    get() =
        flowOf(
            MeasurementType.entries.filter { type ->
                when (type) {
                    MeasurementType.Gram -> !isLiquid
                    MeasurementType.Ounce -> !isLiquid
                    MeasurementType.Milliliter -> isLiquid
                    MeasurementType.FluidOunce -> isLiquid
                    MeasurementType.Package -> totalWeight != null
                    MeasurementType.Serving -> servingWeight != null
                }
            }
        )

private val Food.defaultMeasurement: Flow<Measurement>
    get() =
        flowOf(
            when {
                servingWeight != null -> Measurement.Serving(1.0)
                totalWeight != null -> Measurement.Package(1.0)
                isLiquid -> Measurement.Milliliter(100.0)
                else -> Measurement.Gram(100.0)
            }
        )
