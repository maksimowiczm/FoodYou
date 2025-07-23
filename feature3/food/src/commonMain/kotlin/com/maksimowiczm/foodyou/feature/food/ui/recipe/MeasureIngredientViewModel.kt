package com.maksimowiczm.foodyou.feature.food.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.Food
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class MeasureIngredientViewModel(
    foodId: FoodId,
    foodDatabase: FoodDatabase,
    observeFoodUseCase: ObserveFoodUseCase
) : ViewModel() {
    val productDao = foodDatabase.productDao

    val food: StateFlow<Food?> = observeFoodUseCase.observe(foodId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val possibleMeasurements: StateFlow<Set<MeasurementType>?> = food
        .filterNotNull()
        .map { food -> food.possibleMeasurementTypes.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    val suggestions: StateFlow<Set<Measurement>?> = possibleMeasurements
        .filterNotNull()
        .map { set ->
            set.map { type ->
                when (type) {
                    MeasurementType.Gram -> Measurement.Gram(100f)
                    MeasurementType.Package -> Measurement.Package(1f)
                    MeasurementType.Serving -> Measurement.Serving(1f)
                    MeasurementType.Milliliter -> Measurement.Milliliter(100f)
                }
            }.toSet()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )
}
