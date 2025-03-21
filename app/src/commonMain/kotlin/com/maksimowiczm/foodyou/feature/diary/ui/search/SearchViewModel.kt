package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class SearchViewModel(
    private val addFoodRepository: AddFoodRepository,
    val mealId: Long,
    val date: LocalDate
) : ViewModel() {
    val recentQueries = addFoodRepository.observeProductQueries(
        limit = 20
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }
    val searchQuery = _searchQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val productsWithMeasurements = _searchQuery.flatMapLatest { query ->
        addFoodRepository.queryProducts(
            mealId = mealId,
            date = date,
            query = query
        )
    }.cachedIn(viewModelScope)

    fun onSearch(query: String?) {
        viewModelScope.launch {
            _searchQuery.emit(query?.takeIf { it.isNotBlank() })
        }
    }

    fun onQuickAdd(productId: Long, measurement: WeightMeasurement) {
        viewModelScope.launch {
            addFoodRepository.addMeasurement(
                date = date,
                mealId = mealId,
                productId = productId,
                weightMeasurement = measurement
            )
        }
    }

    fun onQuickRemove(measurementId: Long) {
        viewModelScope.launch {
            addFoodRepository.removeMeasurement(measurementId)
        }
    }
}
