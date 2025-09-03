package com.maksimowiczm.foodyou.feature.food.diary.update.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.UnpackFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.UpdateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.business.fooddiary.domain.suggestions
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.ext.now
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
internal class UpdateFoodDiaryEntryViewModel(
    private val entryId: FoodDiaryEntryId,
    private val updateFoodDiaryEntryUseCase: UpdateFoodDiaryEntryUseCase,
    private val unpackDiaryEntryError: UnpackFoodDiaryEntryUseCase,
    entryRepository: FoodDiaryEntryRepository,
    mealRepository: MealRepository,
    dateProvider: DateProvider,
) : ViewModel() {

    val meals =
        mealRepository
            .observeMeals()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = emptyList(),
            )

    val entry =
        entryRepository
            .observe(entryId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val possibleMeasurementTypes =
        entry
            .filterNotNull()
            .flatMapLatest { entry -> entry.food.possibleMeasurementTypes }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val suggestions: StateFlow<List<Measurement>?> =
        entry
            .filterNotNull()
            .flatMapLatest { entry ->
                entry.food.suggestions.map { (listOf(entry.measurement) + it).distinct() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val today =
        dateProvider
            .observeDate()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = LocalDate.now(),
            )

    private val _uiEvents = Channel<UpdateEntryEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun save(measurement: Measurement, mealId: Long, date: LocalDate) {
        viewModelScope.launch {
            updateFoodDiaryEntryUseCase
                .update(id = entryId, measurement = measurement, mealId = mealId, date = date)
                .fold(
                    onSuccess = { _uiEvents.send(UpdateEntryEvent.Saved) },
                    onFailure = {
                        FoodYouLogger.e(TAG) {
                            "Failed to update diary entry with id $entryId, $it"
                        }
                    },
                )

            _uiEvents.send(UpdateEntryEvent.Saved)
        }
    }

    fun unpack(measurement: Measurement, mealId: Long, date: LocalDate) {
        viewModelScope.launch {
            unpackDiaryEntryError
                .unpack(id = entryId, measurement = measurement, mealId = mealId, date = date)
                .fold(
                    onSuccess = { _uiEvents.send(UpdateEntryEvent.Saved) },
                    onFailure = {
                        FoodYouLogger.e(TAG) {
                            "Failed to unpack diary entry with id $entryId, $it"
                        }
                    },
                )

            _uiEvents.send(UpdateEntryEvent.Saved)
        }
    }

    private companion object {
        const val TAG = "UpdateFoodDiaryEntryViewModel"
    }
}
