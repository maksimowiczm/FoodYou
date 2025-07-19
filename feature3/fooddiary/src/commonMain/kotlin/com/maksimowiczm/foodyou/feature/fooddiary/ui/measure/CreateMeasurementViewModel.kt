package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.domain.defaultMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.domain.toMeasurement
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
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
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class CreateMeasurementViewModel(
    foodDatabase: FoodDatabase,
    foodDiaryDatabase: FoodDiaryDatabase,
    productMapper: ProductMapper,
    observeRecipeUseCase: ObserveRecipeUseCase,
    dateProvider: DateProvider,
    private val foodId: FoodId
) : ViewModel() {
    private val productDao = foodDatabase.productDao
    private val recipeDao = foodDatabase.recipeDao
    private val mealsDao = foodDiaryDatabase.mealDao
    private val measurementDao = foodDiaryDatabase.measurementDao

    val food = when (foodId) {
        is FoodId.Product ->
            productDao
                .observe(foodId.id)
                .filterNotNull()
                .map(productMapper::toModel)

        is FoodId.Recipe -> observeRecipeUseCase(foodId).filterNotNull()
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
    val suggestions: StateFlow<List<Measurement>?> = measurementDao
        .observeMeasurementSuggestions(
            productId = (foodId as? FoodId.Product)?.id,
            recipeId = (foodId as? FoodId.Recipe)?.id,
            limit = 5
        )
        .flatMapLatest { list ->
            food.filterNotNull().map { product ->
                val measurements = list.map { it.toMeasurement() }.filter {
                    when (it) {
                        is Measurement.Gram, is Measurement.Milliliter -> true
                        is Measurement.Package -> product.totalWeight != null
                        is Measurement.Serving -> product.servingWeight != null
                    }
                }.toMutableList()

                // Fill missing measurements
                measurements.add(Measurement.Gram(100f))

                if (product.totalWeight != null) {
                    measurements.add(Measurement.Package(1f))
                }
                if (product.servingWeight != null) {
                    measurements.add(Measurement.Serving(1f))
                }

                measurements.distinct()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
        )

    val possibleMeasurementTypes = food.filterNotNull().map {
        it.let { product ->
            MeasurementType.entries.filter { type ->
                when (type) {
                    MeasurementType.Gram -> true
                    MeasurementType.Milliliter -> true
                    MeasurementType.Package -> product.totalWeight != null
                    MeasurementType.Serving -> product.servingWeight != null
                }
            }
        }
    }.stateIn(
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
            food.filterNotNull().map { product ->
                list.map { it.toMeasurement() }.filter {
                    when (it) {
                        is Measurement.Gram -> true
                        is Measurement.Milliliter -> true
                        is Measurement.Package -> product.totalWeight != null
                        is Measurement.Serving -> product.servingWeight != null
                    }
                }.ifEmpty {
                    listOf(product.defaultMeasurement)
                }.first()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(2_000),
            initialValue = null
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
        val entity = MeasurementEntity(
            mealId = mealId,
            epochDay = date.toEpochDays(),
            productId = (foodId as? FoodId.Product)?.id,
            recipeId = (foodId as? FoodId.Recipe)?.id,
            measurement = measurement.type,
            quantity = measurement.rawValue,
            createdAt = Clock.System.now().epochSeconds
        )

        viewModelScope.launch {
            measurementDao.insertMeasurement(entity)
            eventBus.send(MeasurementEvent.Saved)
        }
    }
}
