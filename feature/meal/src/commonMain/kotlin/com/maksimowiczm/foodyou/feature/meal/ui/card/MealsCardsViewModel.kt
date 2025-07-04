package com.maksimowiczm.foodyou.feature.meal.ui.card

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.domain.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.feature.meal.data.observeMealCardsLayout
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

internal class MealsCardsViewModel(
    private val measurementRepository: MeasurementRepository,
    private val dataStore: DataStore<Preferences>,
    private val observeMealsUseCase: ObserveMealsUseCase
) : ViewModel() {

    val layout = dataStore.observeMealCardsLayout().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking { dataStore.observeMealCardsLayout().first() }
    )

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

    fun setDate(date: LocalDate) = launch {
        dateState.value = date
    }

    fun onDeleteMeasurement(measurementId: Long) = launch {
        measurementRepository.removeMeasurement(measurementId)
    }

    fun unpackRecipe(
        recipeId: FoodId.Recipe,
        mealId: Long,
        measurement: Measurement,
        measurementId: Long
    ) = launch {
        val date = dateState.value

        if (date == null) {
            Logger.w(TAG) { "Date is not set, cannot unpack recipe." }
            return@launch
        }

        measurementRepository.unpackRecipe(
            date = date,
            mealId = mealId,
            recipeId = recipeId,
            measurement = measurement
        )

        measurementRepository.removeMeasurement(measurementId)
    }

    private companion object {
        const val TAG = "MealsCardsViewModel"
    }
}
