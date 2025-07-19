package com.maksimowiczm.foodyou.feature.fooddiary.ui.measure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.Product
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.domain.toMeasurement
import com.maksimowiczm.foodyou.feature.measurement.data.Measurement as MeasurementType
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.rawValue
import com.maksimowiczm.foodyou.feature.measurement.domain.type
import kotlin.collections.map
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

internal class UpdateProductMeasurementViewModel(
    foodDatabase: FoodDatabase,
    foodDiaryDatabase: FoodDiaryDatabase,
    productMapper: ProductMapper,
    dateProvider: DateProvider,
    measurementId: Long
) : ViewModel() {

    private val measurementDao = foodDiaryDatabase.measurementDao
    private val mealsDao = foodDiaryDatabase.mealDao
    private val productDao = foodDatabase.productDao

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
        when {
            measurement.productId != null ->
                productDao
                    .observe(measurement.productId)
                    .filterNotNull()
                    .map(productMapper::toModel)

            else -> error("Measurement does not have a productId")
        }
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
        when (food) {
            is Product -> measurementDao.observeMeasurementSuggestions(food.id.id, 5).map { list ->
                val measurements = list.map { it.toMeasurement() }.filter {
                    when (it) {
                        is Measurement.Gram, is Measurement.Milliliter -> true
                        is Measurement.Package -> food.packageWeight != null
                        is Measurement.Serving -> food.servingWeight != null
                    }
                }.toMutableList()

                // Fill missing measurements
                measurements.add(Measurement.Gram(100f))

                if (food.packageWeight != null) {
                    measurements.add(Measurement.Package(1f))
                }
                if (food.servingWeight != null) {
                    measurements.add(Measurement.Serving(1f))
                }

                measurements.distinct()
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val possibleMeasurementTypes = food.filterNotNull().map {
        when (it) {
            is Product -> it.let { product ->
                MeasurementType.entries.filter { type ->
                    when (type) {
                        MeasurementType.Gram -> true
                        MeasurementType.Milliliter -> true
                        MeasurementType.Package -> product.packageWeight != null
                        MeasurementType.Serving -> product.servingWeight != null
                    }
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    private val eventBus = Channel<MeasurementEvent>()
    val events = eventBus.receiveAsFlow()

    fun deleteProduct() {
        viewModelScope.launch {
            val food = food.value

            if (food == null) {
                Logger.w(TAG) { " Food is null, cannot delete product measurement" }
                return@launch
            }

            when (food) {
                is Product -> {
                    val product = productDao.observe(food.id.id).firstOrNull()
                    if (product != null) {
                        productDao.delete(product)
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

    private companion object {
        const val TAG = "UpdateProductMeasurementViewModel"
    }
}
