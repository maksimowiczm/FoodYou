package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.command.DeleteFoodCommand
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodEventsQuery
import com.maksimowiczm.foodyou.business.food.application.query.ObserveMeasurementSuggestionsQuery
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.weight
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommand
import com.maksimowiczm.foodyou.feature.food.diary.shared.usecase.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.food.shared.presentation.defaultMeasurement
import com.maksimowiczm.foodyou.feature.food.shared.presentation.possibleMeasurementTypes
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.ui.ext.now
import kotlin.collections.emptyList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class AddEntryViewModel(
    queryBus: QueryBus,
    private val commandBus: CommandBus,
    observeFoodUseCase: ObserveFoodUseCase,
    observeMealsUseCase: ObserveMealsUseCase,
    dateProvider: DateProvider,
    private val foodId: FoodId,
) : ViewModel() {

    private val _uiEventBus = Channel<AddEntryEvent>()
    val uiEvents = _uiEventBus.receiveAsFlow()

    private val domainFood =
        observeFoodUseCase
            .observe(foodId)
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                replay = 1,
            )

    val food =
        domainFood
            .mapIfNotNull {
                when (it) {
                    is Product -> ProductModel(it)
                    is Recipe -> RecipeModel(it)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val foodEvents =
        queryBus
            .dispatch(ObserveFoodEventsQuery(foodId))
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = emptyList(),
            )

    val meals =
        observeMealsUseCase
            .observe()
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

    val possibleMeasurementTypes =
        domainFood
            .filterNotNull()
            .map { it.possibleMeasurementTypes }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestedMeasurement: StateFlow<Measurement?> =
        domainFood
            .filterNotNull()
            .flatMapLatest { food ->
                suggestions.filterNotNull().map { list ->
                    list.firstOrNull() ?: food.defaultMeasurement
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun deleteFood() {
        viewModelScope.launch {
            commandBus
                .dispatch(DeleteFoodCommand(foodId))
                .fold(
                    onSuccess = {
                        FoodYouLogger.d(TAG) { "Food with ID $foodId deleted successfully." }
                        _uiEventBus.send(AddEntryEvent.FoodDeleted)
                    },
                    onFailure = { e ->
                        FoodYouLogger.e(TAG) { "Failed to delete food with ID $foodId" }
                    },
                )
        }
    }

    fun addEntry(measurement: Measurement, mealId: Long, date: LocalDate) {
        viewModelScope.launch {
            val food = domainFood.firstOrNull()
            if (food == null) {
                FoodYouLogger.e(TAG) { "No food found, cannot add entry." }
                return@launch
            }
            val diaryFood = food.toDiaryFood()

            commandBus
                .dispatch(
                    CreateDiaryEntryCommand(
                        foodId = food.id,
                        measurement = measurement,
                        mealId = mealId,
                        date = date,
                        food = diaryFood,
                    )
                )
                .fold(
                    onSuccess = {
                        FoodYouLogger.d(TAG) { "Diary entry created successfully" }
                        _uiEventBus.send(AddEntryEvent.EntryAdded)
                    },
                    onFailure = { FoodYouLogger.e(TAG) { "Failed to create diary entry" } },
                )
        }
    }

    fun unpack(measurement: Measurement, mealId: Long, date: LocalDate) {
        viewModelScope.launch {
            val food = domainFood.firstOrNull()
            if (food !is Recipe) {
                FoodYouLogger.e(TAG) { "Food is not a recipe, cannot unpack." }
                return@launch
            }

            val weight = measurement.weight(food)
            food.unpack(weight).map { (food, measurement) ->
                val diaryFood = food.toDiaryFood()

                commandBus
                    .dispatch(
                        CreateDiaryEntryCommand(
                            foodId = food.id,
                            measurement = measurement,
                            mealId = mealId,
                            date = date,
                            food = diaryFood,
                        )
                    )
                    .fold(
                        onSuccess = {
                            FoodYouLogger.d(TAG) {
                                "Diary entry for ingredient created successfully"
                            }
                        },
                        onFailure = {
                            FoodYouLogger.e(TAG) { "Failed to create diary entry for ingredient" }
                        },
                    )

                _uiEventBus.send(AddEntryEvent.EntryAdded)
            }
        }
    }

    private companion object {
        const val TAG = "AddEntryViewModel"
    }
}
