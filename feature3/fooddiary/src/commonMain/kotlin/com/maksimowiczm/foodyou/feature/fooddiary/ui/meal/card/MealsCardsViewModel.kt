package com.maksimowiczm.foodyou.feature.fooddiary.ui.meal.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class MealsCardsViewModel(
    foodDiaryDatabase: FoodDiaryDatabase,
    private val observeMealsUseCase: ObserveMealsUseCase
) : ViewModel() {
    private val measurementDao = foodDiaryDatabase.measurementDao

    private val dateState = MutableStateFlow<LocalDate?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val meals = dateState
        .filterNotNull()
        .flatMapLatest { observeMealsUseCase(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(60_000),
            initialValue = null
        )

    fun setDate(date: LocalDate) {
        viewModelScope.launch {
            dateState.value = date
        }
    }

    fun onDeleteMeasurement(measurementId: Long) {
        viewModelScope.launch {
            val measurement = measurementDao.observeMeasurementById(measurementId).first()

            if (measurement == null) {
                Logger.e(TAG) { "Measurement with id $measurementId not found" }
            } else {
                measurementDao.deleteMeasurement(measurement)
            }
        }
    }

    private companion object {
        const val TAG = "MealsCardsViewModel"
    }
}
