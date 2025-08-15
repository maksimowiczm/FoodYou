package com.maksimowiczm.foodyou.feature.food.diary.update.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UnpackDiaryEntryCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateDiaryEntryCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryEntryQuery
import com.maksimowiczm.foodyou.business.fooddiary.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.business.fooddiary.domain.suggestions
import com.maksimowiczm.foodyou.feature.food.diary.shared.usecase.ObserveMealsUseCase
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
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
internal class UpdateEntryViewModel(
    private val entryId: Long,
    queryBus: QueryBus,
    private val commandBus: CommandBus,
    observeMealsUseCase: ObserveMealsUseCase,
    dateProvider: DateProvider,
) : ViewModel() {

    val meals =
        observeMealsUseCase
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = emptyList(),
            )

    val entry =
        queryBus
            .dispatch(ObserveDiaryEntryQuery(entryId))
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
            commandBus
                .dispatch(
                    UpdateDiaryEntryCommand(
                        id = entryId,
                        measurement = measurement,
                        mealId = mealId,
                        date = date,
                    )
                )
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
            commandBus
                .dispatch(
                    UnpackDiaryEntryCommand(
                        id = entryId,
                        measurement = measurement,
                        mealId = mealId,
                        date = date,
                    )
                )
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
        const val TAG = "UpdateEntryViewModel"
    }
}
