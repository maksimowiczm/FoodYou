package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.addfood.AddFoodFeature
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.model.WeightMeasurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class SearchViewModel(
    private val addFoodRepository: AddFoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val mealId: Long
    val date: LocalDate

    init {
        val (epochDay, meal) = savedStateHandle.toRoute<AddFoodFeature.Route>()

        this.mealId = meal
        this.date = LocalDate.fromEpochDays(epochDay)
    }

    val totalCalories = addFoodRepository.observeTotalCalories(
        date = date,
        mealId = mealId
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = runBlocking {
            addFoodRepository.observeTotalCalories(
                date = date,
                mealId = mealId
            ).first()
        }
    )

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
