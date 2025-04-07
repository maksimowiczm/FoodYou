package com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.ObserveRecentQueriesUseCase
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.ObserveSearchFoodUseCase
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.domain.SearchFoodItem
import com.maksimowiczm.foodyou.feature.diary.core.data.measurement.MeasurementRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SearchFoodViewModel(
    private val mealId: Long,
    private val date: LocalDate,
    private val measurementRepository: MeasurementRepository,
    observeRecentQueriesUseCase: ObserveRecentQueriesUseCase,
    observeSearchFoodUseCase: ObserveSearchFoodUseCase
) : ViewModel() {
    private val mutableSearchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }

    val searchQuery = mutableSearchQuery.shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        replay = 1
    )

    val recentQueries = observeRecentQueriesUseCase(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = mutableSearchQuery.flatMapLatest { query ->
        observeSearchFoodUseCase(
            query = query,
            mealId = mealId,
            date = date,
            cache = viewModelScope
        )
    }.cachedIn(viewModelScope)

    fun onSearch(query: String?) {
        viewModelScope.launch {
            mutableSearchQuery.emit(query)
        }
    }

    fun onQuickAdd(item: SearchFoodItem) {
        viewModelScope.launch {
            measurementRepository.addMeasurement(
                date = date,
                mealId = mealId,
                foodId = item.food.id,
                measurement = item.measurement
            )
        }
    }

    fun onQuickRemove(item: SearchFoodItem) {
        viewModelScope.launch {
            item.measurementId?.let {
                measurementRepository.removeMeasurement(item.measurementId)
            }
        }
    }
}
