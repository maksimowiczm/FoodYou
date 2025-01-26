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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class SearchViewModel(
    private val addFoodRepository: AddFoodRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val date: LocalDate
    val meal: Meal

    // Use shared flow which allows emitting equal values
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

    val queryState: MutableStateFlow<QueryResult<List<ProductWithWeightMeasurement>>> =
        MutableStateFlow(QueryResult.loading(emptyList()))

    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        viewModelScope.launch {
            _searchQuery.flatMapLatest { query ->
                addFoodRepository.queryProducts(
                    meal = meal,
                    date = date,
                    query = query
                )
            }.collectLatest { queryResult ->
                queryState.value = queryResult
            }
        }
    }

    fun onSearch(query: String?) {
        val searchQuery = query?.trim()?.ifBlank { null }
        _searchQuery.tryEmit(searchQuery)
    }

    fun onRetry() {
        // Update only twice, when user clicks retry and when query is fully loaded

        queryState.value = queryState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            delay(300L)

            addFoodRepository.queryProducts(
                meal = meal,
                date = date,
                query = _searchQuery.replayCache.first()
            ).collectLatest { queryResult ->
                if (!queryResult.isLoading) {
                    queryState.value = queryResult
                    cancel()
                }
            }
        }
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

    val totalCalories = addFoodRepository.observeMeasuredProducts(
        meal = meal,
        date = date
    ).map { list ->
        list.sumOf { it.calories }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
        initialValue = 0
    )

    val recentQueries = addFoodRepository.observeProductQueries(
        limit = 20
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
        initialValue = emptyList()
    )

    private companion object {
        // 30 seconds, because user often navigate up and down
        private const val STOP_TIMEOUT = 30_000L
    }
}
