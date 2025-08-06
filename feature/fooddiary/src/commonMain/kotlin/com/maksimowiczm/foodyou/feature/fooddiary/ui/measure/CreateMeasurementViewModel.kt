package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEventMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.food.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.feature.food.domain.weight
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.CreateMeasurementUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.defaultMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.domain.toMeasurement
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapValues
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class CreateMeasurementViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    observeMeasurementSuggestionsUseCase: ObserveMeasurementSuggestionsUseCase,
    foodDatabase: FoodDatabase,
    foodDiaryDatabase: FoodDiaryDatabase,
    dateProvider: DateProvider,
    private val foodEventMapper: FoodEventMapper,
    private val createMeasurementUseCase: CreateMeasurementUseCase,
    private val foodId: FoodId
) : ViewModel() {
    private val productDao = foodDatabase.productDao
    private val recipeDao = foodDatabase.recipeDao
    private val mealsDao = foodDiaryDatabase.mealDao
    private val measurementDao = foodDiaryDatabase.measurementDao
    private val foodEventDao = foodDatabase.foodEventDao

    val food = observeFoodUseCase.observe(foodId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val meals = mealsDao.observeMeals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = emptyList()
    )

    val today = dateProvider.observeDate().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = LocalDate.now()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val suggestions: StateFlow<List<Measurement>?> = food.filterNotNull().flatMapLatest { food ->
        observeMeasurementSuggestionsUseCase.observe(
            food = food,
            limit = 5
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val possibleMeasurementTypes = food
        .filterNotNull()
        .map { food -> food.possibleMeasurementTypes }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedMeasurement = measurementDao
        .observeMeasurementSuggestions(
            productId = (foodId as? FoodId.Product)?.id,
            recipeId = (foodId as? FoodId.Recipe)?.id,
            limit = 1
        )
        .flatMapLatest { list ->
            // Choose latest or default
            food.filterNotNull().map { product ->
                list.map { it.toMeasurement() }.firstOrNull {
                    when (it) {
                        is Measurement.Gram -> true
                        is Measurement.Milliliter -> true
                        is Measurement.Package -> product.totalWeight != null
                        is Measurement.Serving -> product.servingWeight != null
                    }
                } ?: product.defaultMeasurement
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    val foodEvents = foodEventDao
        .observeEvents(
            productId = (foodId as? FoodId.Product)?.id,
            recipeId = (foodId as? FoodId.Recipe)?.id
        )
        .mapValues(foodEventMapper::toModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = emptyList()
        )

    private val eventBus = Channel<MeasurementEvent>()
    val events = eventBus.receiveAsFlow()

    fun deleteFood() {
        viewModelScope.launch {
            when (foodId) {
                is FoodId.Product -> {
                    val product = productDao.observe(foodId.id).firstOrNull()

                    if (product != null) {
                        productDao.delete(product)
                        eventBus.send(MeasurementEvent.Deleted)
                    }
                }

                is FoodId.Recipe -> {
                    val recipe = recipeDao.observe(foodId.id).firstOrNull()

                    if (recipe != null) {
                        recipeDao.delete(recipe)
                        eventBus.send(MeasurementEvent.Deleted)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun createMeasurement(measurement: Measurement, mealId: Long, date: LocalDate) {
        viewModelScope.launch {
            createMeasurementUseCase.createMeasurement(
                measurement = measurement,
                foodId = foodId,
                mealId = mealId,
                date = date
            )
            eventBus.send(MeasurementEvent.Saved)
        }
    }

    @OptIn(ExperimentalTime::class)
    fun unpack(measurement: Measurement, mealId: Long, date: LocalDate) {
        val recipe = food.value as? Recipe

        if (recipe == null) {
            Logger.w(TAG) { "Unpacking measurement failed: food is not a recipe." }
            return
        }

        val weight = measurement.weight(recipe)
        val ingredients = recipe.measuredIngredients(weight)

        viewModelScope.launch {
            ingredients.forEach { ingredient ->
                createMeasurementUseCase.createMeasurement(
                    measurement = ingredient.measurement,
                    foodId = ingredient.food.id,
                    mealId = mealId,
                    date = date
                )
            }

            eventBus.send(MeasurementEvent.Saved)
        }
    }

    private companion object {
        private const val TAG = "CreateMeasurementViewModel"
    }
}
