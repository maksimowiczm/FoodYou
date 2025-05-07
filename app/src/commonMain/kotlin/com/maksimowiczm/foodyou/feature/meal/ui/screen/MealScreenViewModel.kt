package com.maksimowiczm.foodyou.feature.meal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

class MealScreenViewModel(
    mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val mealId: Long,
    private val date: LocalDate
) : ViewModel() {
    val meal = mealRepository
        .observeMeal(mealId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    val foods = measurementRepository.observeMeasurements(
        mealId = mealId,
        date = date
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    fun onDeleteMeasurement(measurementId: MeasurementId) = launch {
        measurementRepository.removeMeasurement(measurementId)
    }
}
