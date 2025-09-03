package com.maksimowiczm.foodyou.feature.food.diary.add.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.application.DeleteFoodUseCase
import com.maksimowiczm.foodyou.business.food.application.ObserveFoodUseCase
import com.maksimowiczm.foodyou.business.food.application.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.business.food.domain.FoodEventRepository
import com.maksimowiczm.foodyou.business.food.domain.Product
import com.maksimowiczm.foodyou.business.food.domain.Recipe
import com.maksimowiczm.foodyou.business.food.domain.defaultMeasurement
import com.maksimowiczm.foodyou.business.food.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.business.fooddiary.application.CreateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.ui.ext.now
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapIfNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
internal class AddEntryViewModel(
    private val createFoodDiaryEntryUseCase: CreateFoodDiaryEntryUseCase,
    observeFoodUseCase: ObserveFoodUseCase,
    foodEventRepository: FoodEventRepository,
    private val deleteFoodUseCase: DeleteFoodUseCase,
    observeMeasurementSuggestionsUseCase: ObserveMeasurementSuggestionsUseCase,
    mealRepository: MealRepository,
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

    val food: StateFlow<FoodModel?> =
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
        foodEventRepository
            .observeFoodEvents(foodId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = emptyList(),
            )

    val meals =
        mealRepository
            .observeMeals()
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
            .flatMapLatest { it.possibleMeasurementTypes }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val suggestions: StateFlow<List<Measurement>?> =
        observeMeasurementSuggestionsUseCase
            .observe(foodId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val suggestedMeasurement: StateFlow<Measurement?> =
        domainFood
            .filterNotNull()
            .flatMapLatest { food ->
                suggestions.filterNotNull().flatMapLatest { list ->
                    list.firstOrNull()?.let(::flowOf) ?: food.defaultMeasurement
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun deleteFood() {
        viewModelScope.launch {
            deleteFoodUseCase
                .deleteFood(foodId)
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

            createFoodDiaryEntryUseCase
                .createDiaryEntry(
                    foodId = food.id,
                    measurement = measurement,
                    mealId = mealId,
                    date = date,
                    food = diaryFood,
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

            val weight = food.weight(measurement)
            food.unpack(weight).map { (food, measurement) ->
                val diaryFood = food.toDiaryFood()

                createFoodDiaryEntryUseCase
                    .createDiaryEntry(
                        foodId = food.id,
                        measurement = measurement,
                        mealId = mealId,
                        date = date,
                        food = diaryFood,
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
