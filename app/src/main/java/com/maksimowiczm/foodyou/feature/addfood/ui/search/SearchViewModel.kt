package com.maksimowiczm.foodyou.feature.addfood.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.navigation.AddFoodRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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

    val measuredProducts = addFoodRepository.observeMeasuredProducts(meal, date).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    // TODO
    //  idk if thi is the right way to do this but I'm not sure how to do it better
    fun getRecentQueries(): Flow<List<String>> {
        val flow = flow {
            emit(addFoodRepository.observeProductQueries(20).first().map { it.query })
        }

        return flow
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

    fun onSearch(query: String?) {
        val searchQuery = query?.trim()?.ifBlank { null }
        _searchQuery.tryEmit(searchQuery)
    }

    fun onRetry() {
        _searchQuery.tryEmit(_searchQuery.replayCache.first())
    }

    fun onQuickRemove(model: ProductWithWeightMeasurement) {
        viewModelScope.launch {
            model.measurementId?.let { addFoodRepository.removeFood(it) }
        }
    }

    suspend fun onQuickAdd(model: ProductWithWeightMeasurement): Long {
        return addFoodRepository.addFood(
            date = date,
            meal = meal,
            productId = model.product.id,
            weightMeasurement = model.measurement
        )
    }
}
