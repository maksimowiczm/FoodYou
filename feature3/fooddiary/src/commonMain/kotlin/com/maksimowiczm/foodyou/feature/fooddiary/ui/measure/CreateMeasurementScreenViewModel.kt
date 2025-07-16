package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.Measurement as MeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.domain.defaultMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.domain.rawValue
import com.maksimowiczm.foodyou.feature.fooddiary.domain.toMeasurement
import com.maksimowiczm.foodyou.feature.fooddiary.domain.type
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
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

internal class CreateMeasurementScreenViewModel(
    foodDatabase: FoodDatabase,
    foodDiaryDatabase: FoodDiaryDatabase,
    productMapper: ProductMapper,
    dateProvider: DateProvider,
    private val productId: FoodId.Product
) : ViewModel() {
    private val productsDao = foodDatabase.productDao
    private val mealsDao = foodDiaryDatabase.mealDao
    private val measurementDao = foodDiaryDatabase.measurementDao

    val product = productsDao
        .observe(productId.id)
        .filterNotNull()
        .map(productMapper::toModel)
        .stateIn(
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
        .observeMeasurementSuggestions(productId.id, 5)
        .flatMapLatest { list ->
            product.filterNotNull().map { product ->
                val measurements = list.map { it.toMeasurement() }.filter {
                    when (it) {
                        is Measurement.Gram, is Measurement.Milliliter -> true
                        is Measurement.Package -> product.packageWeight != null
                        is Measurement.Serving -> product.servingWeight != null
                    }
                }.toMutableList()

                // Fill missing measurements
                measurements.add(Measurement.Gram(100f))

                if (product.packageWeight != null) {
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

    val possibleMeasurementTypes = product.filterNotNull().map {
        it.let { product ->
            MeasurementType.entries.filter { type ->
                when (type) {
                    MeasurementType.Gram -> true
                    MeasurementType.Milliliter -> true
                    MeasurementType.Package -> product.packageWeight != null
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
        .observeMeasurementSuggestions(productId.id, 1)
        .flatMapLatest { list ->
            product.filterNotNull().map { product ->
                list.map { it.toMeasurement() }.filter {
                    when (it) {
                        is Measurement.Gram -> true
                        is Measurement.Milliliter -> true
                        is Measurement.Package -> product.packageWeight != null
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

    fun deleteProduct() {
        viewModelScope.launch {
            val product = productsDao.observe(productId.id).firstOrNull()

            if (product != null) {
                productsDao.delete(product)
                eventBus.send(MeasurementEvent.Deleted)
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    fun createMeasurement(measurement: Measurement, mealId: Long, date: LocalDate) {
        val entity = MeasurementEntity(
            mealId = mealId,
            epochDay = date.toEpochDays(),
            productId = productId.id,
            recipeId = null,
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
