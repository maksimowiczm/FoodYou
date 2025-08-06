package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodEventMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.domain.Recipe
import com.maksimowiczm.foodyou.feature.food.domain.possibleMeasurementTypes
import com.maksimowiczm.foodyou.feature.food.domain.weight
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.toMeasurement
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
import kotlin.time.Clock
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

internal class UpdateMeasurementViewModel(
    observeFoodUseCase: ObserveFoodUseCase,
    observeMeasurementSuggestionsUseCase: ObserveMeasurementSuggestionsUseCase,
    foodDatabase: FoodDatabase,
    foodDiaryDatabase: FoodDiaryDatabase,
    dateProvider: DateProvider,
    foodEventMapper: FoodEventMapper,
    private val measurementId: Long
) : ViewModel() {

    private val measurementDao = foodDiaryDatabase.measurementDao
    private val mealsDao = foodDiaryDatabase.mealDao
    private val productDao = foodDatabase.productDao
    private val recipeDao = foodDatabase.recipeDao
    private val foodEventDao = foodDatabase.foodEventDao

    private val measurementEntity =
        foodDiaryDatabase.measurementDao.observeMeasurementById(measurementId).filterNotNull()

    val mealId = measurementEntity.map { it.mealId }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val measurement = measurementEntity.map { it.toMeasurement() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val measurementDate = measurementEntity.map { LocalDate.fromEpochDays(it.epochDay) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = LocalDate.now()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val food = measurementEntity.flatMapLatest { measurement ->
        val foodId = when {
            measurement.productId != null -> FoodId.Product(measurement.productId)
            measurement.recipeId != null -> FoodId.Recipe(measurement.recipeId)
            else -> error("Measurement does not have a productId or recipeId")
        }

        observeFoodUseCase.observe(foodId)
    }.stateIn(
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
    val foodEvents = food
        .filterNotNull()
        .flatMapLatest { food ->
            foodEventDao.observeEvents(
                productId = (food.id as? FoodId.Product)?.id,
                recipeId = (food.id as? FoodId.Recipe)?.id
            )
        }
        .mapValues(foodEventMapper::toModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = emptyList()
        )

    private val eventBus = Channel<MeasurementEvent>()
    val events = eventBus.receiveAsFlow()

    fun deleteProduct() {
        val food = food.value

        if (food == null) {
            Logger.w(TAG) { " Food is null, cannot delete product measurement" }
            return
        }

        viewModelScope.launch {
            when (food) {
                is Product -> {
                    val product = productDao.observe(food.id.id).firstOrNull()
                    if (product != null) {
                        productDao.delete(product)
                        eventBus.send(MeasurementEvent.Deleted)
                    }
                }

                is Recipe -> {
                    val recipe = recipeDao.observe(food.id.id).firstOrNull()
                    if (recipe != null) {
                        recipeDao.delete(recipe)
                        eventBus.send(MeasurementEvent.Deleted)
                    }
                }
            }
        }
    }

    fun updateMeasurement(measurement: Measurement, mealId: Long, date: LocalDate) {
        viewModelScope.launch {
            val entity = measurementEntity.firstOrNull()

            if (entity == null) {
                Logger.w(TAG) { "Measurement entity is null, cannot update measurement" }
                return@launch
            }

            val updatedEntity = entity.copy(
                measurement = measurement.type,
                quantity = measurement.rawValue,
                mealId = mealId,
                epochDay = date.toEpochDays()
            )

            measurementDao.updateMeasurement(updatedEntity)

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
        val now = Clock.System.now()

        val measurements = ingredients.map { ingredient ->
            val productId = (ingredient.food.id as? FoodId.Product)?.id
            val recipeId = (ingredient.food.id as? FoodId.Recipe)?.id

            MeasurementEntity(
                mealId = mealId,
                epochDay = date.toEpochDays(),
                productId = productId,
                recipeId = recipeId,
                measurement = ingredient.measurement.type,
                quantity = ingredient.measurement.rawValue,
                createdAt = now.epochSeconds
            )
        }

        viewModelScope.launch {
            measurementDao.replaceMeasurement(measurementId, measurements)
            eventBus.send(MeasurementEvent.Saved)
        }
    }

    private companion object {
        const val TAG = "UpdateMeasurementViewModel"
    }
}
