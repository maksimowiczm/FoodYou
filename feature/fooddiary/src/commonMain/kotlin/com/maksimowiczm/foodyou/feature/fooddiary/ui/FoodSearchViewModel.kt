package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalTime::class)
internal class FoodSearchViewModel(database: FoodDiaryDatabase, mealId: Long) : ViewModel() {

    val measurementDao = database.measurementDao

    val meal = database.mealDao.observeMealById(mealId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    private val measurementSizeChannel = Channel<Int>()
    val measurementSizeEvents = measurementSizeChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val latestMeasurement = measurementDao.getLatestMeasurement()
            val afterEpoch = latestMeasurement?.createdAt?.times(1000)
                ?: Clock.System.now().toEpochMilliseconds()

            measurementDao.observeMeasurementsAfter(afterEpoch).collectLatest { measurements ->
                measurementSizeChannel.send(measurements.size)
            }
        }
    }
}
