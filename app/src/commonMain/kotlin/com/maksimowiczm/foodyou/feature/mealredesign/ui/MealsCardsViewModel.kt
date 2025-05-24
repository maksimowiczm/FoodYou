package com.maksimowiczm.foodyou.feature.mealredesign.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.combine
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.feature.meal.data.observeMealCardsLayout
import com.maksimowiczm.foodyou.feature.mealredesign.domain.Meal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

internal class MealsCardsViewModel(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val layout = dataStore.observeMealCardsLayout().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = runBlocking { dataStore.observeMealCardsLayout().first() }
    )

    private val dateState = MutableStateFlow<LocalDate?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val meals = dateState.filterNotNull().flatMapLatest { date ->
        mealRepository.observeMeals().flatMapLatest { meals ->
            if (meals.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            meals.map { meal ->
                measurementRepository.observeMeasurements(
                    mealId = meal.id,
                    date = date
                ).map { food ->
                    Meal(
                        id = meal.id,
                        name = meal.name,
                        from = meal.from,
                        to = meal.to,
                        rank = meal.rank,
                        food = food
                    )
                }
            }.combine { it.toList() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(60_000),
        initialValue = null
    )

    fun setDate(date: LocalDate) = launch {
        dateState.value = date
    }

    fun onDeleteMeasurement(measurementId: MeasurementId) = launch {
        measurementRepository.removeMeasurement(measurementId)
    }
}
