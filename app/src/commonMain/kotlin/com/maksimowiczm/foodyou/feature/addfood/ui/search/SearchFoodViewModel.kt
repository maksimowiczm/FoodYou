package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.domain.repository.MeasurementRepository
import com.maksimowiczm.foodyou.core.domain.repository.SearchRepository
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.model.SearchFoodItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

internal class SearchFoodViewModel(
    private val mealId: Long,
    private val date: LocalDate,
    private val measurementRepository: MeasurementRepository,
    private val addFoodRepository: AddFoodRepository,
    searchRepository: SearchRepository
) : ViewModel() {
    private val mutableSearchQuery = MutableStateFlow<String?>(null)

    val recentQueries = searchRepository.observeRecentQueries(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = mutableSearchQuery.flatMapLatest { query ->
        addFoodRepository.queryFood(
            query = query,
            mealId = mealId,
            date = date
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
                foodId = item.foodId,
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
