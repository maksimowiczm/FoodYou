package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductSearchModel
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class SearchViewModel(
    private val addFoodRepository: AddFoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val date: LocalDate
    val meal: Meal

    // Use shared flow to allow emitting equal values
    private val _searchQuery = MutableSharedFlow<String?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        val (epochDay, meal) = savedStateHandle.toRoute<AddFoodRoute.Search>()
        date = LocalDate.ofEpochDay(epochDay)
        this.meal = meal
        _searchQuery.tryEmit(null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val queryState = _searchQuery.flatMapLatest {
        addFoodRepository.queryProducts(
            meal = meal,
            date = date,
            query = it
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = QueryResult.loading(emptyList())
    )

    fun onSearch(query: String) {
        val searchQuery = query.trim().ifBlank { null }
        _searchQuery.tryEmit(searchQuery)
    }

    fun onRetry() {
        _searchQuery.tryEmit(_searchQuery.replayCache.first())
    }

    fun onQuickRemove(model: ProductSearchModel) {
        viewModelScope.launch {
            model.measurementId?.let { addFoodRepository.removeFood(it) }
        }
    }

    suspend fun onQuickAdd(model: ProductSearchModel): Long {
        return addFoodRepository.addFood(
            date = date,
            meal = meal,
            productId = model.product.id,
            weightMeasurement = model.measurement
        )
    }
}
