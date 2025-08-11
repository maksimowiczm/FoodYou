package com.maksimowiczm.foodyou.feature.food.recipe.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.query.ObserveMeasurementSuggestionsQuery
import com.maksimowiczm.foodyou.business.food.domain.Food
import com.maksimowiczm.foodyou.feature.food.shared.presentation.possibleMeasurementTypes
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.domain.measurement.MeasurementType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class MeasureIngredientViewModel(
    foodId: FoodId,
    observeFoodUseCase: ObserveFoodUseCase,
    queryBus: QueryBus,
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
            .map { food -> food.possibleMeasurementTypes }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val suggestions: StateFlow<List<Measurement>?> =
        queryBus
            .dispatch(ObserveMeasurementSuggestionsQuery(foodId))
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}
