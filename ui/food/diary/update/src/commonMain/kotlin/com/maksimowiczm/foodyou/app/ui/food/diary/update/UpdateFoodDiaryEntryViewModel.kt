package com.maksimowiczm.foodyou.app.ui.food.diary.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFood
import com.maksimowiczm.foodyou.fooddiary.domain.entity.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UnpackFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UpdateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.shared.common.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.extension.now
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.domain.measurement.MeasurementType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapValues
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

// These extensions will probably be moved into business when user would be able to choose between
// metric and imperial measurements. This is why they are wrapped in Flow, so they can be
// easily converted to the appropriate measurement system later.

val DiaryFood.possibleMeasurementTypes: Flow<List<MeasurementType>>
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

val DiaryFood.suggestions: Flow<List<Measurement>>
    get() =
        possibleMeasurementTypes.mapValues {
            when (it) {
                MeasurementType.Gram -> Measurement.Gram(Measurement.Gram.DEFAULT)
                MeasurementType.Ounce -> Measurement.Ounce(Measurement.Ounce.DEFAULT)
                MeasurementType.Package -> Measurement.Package(Measurement.Package.DEFAULT)
                MeasurementType.Serving -> Measurement.Serving(Measurement.Serving.DEFAULT)
                MeasurementType.Milliliter -> Measurement.Milliliter(Measurement.Milliliter.DEFAULT)
                MeasurementType.FluidOunce -> Measurement.FluidOunce(Measurement.FluidOunce.DEFAULT)
            }
        }
