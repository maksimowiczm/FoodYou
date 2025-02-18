package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
        val (epochDay, meal) = savedStateHandle.toRoute<AddFoodFeature>()

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

    // Use shared flow to allow emitting equal values multiple times
    private val _searchQuery = MutableSharedFlow<String?>(replay = 1)
    val searchQuery: StateFlow<String?> = _searchQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    init {
        _searchQuery.tryEmit(null)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = _searchQuery.flatMapLatest { query ->
        addFoodRepository.queryProducts(
            mealId = mealId,
            date = date,
            query = query,
            localOnly = query == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = QueryResult.loading(emptyList())
    )

    fun onSearch(query: String?) {
        viewModelScope.launch {
            _searchQuery.emit(query?.takeIf { it.isNotBlank() })
        }
    }

    fun onRetry() {
        viewModelScope.launch {
            _searchQuery.emit(searchQuery.value)
        }
    }

    fun onQuickAdd(
        productId: Long,
        measurement: WeightMeasurement
    ) {
        viewModelScope.launch {
            addFoodRepository.addMeasurement(
                date = date,
                mealId = mealId,
                productId = productId,
                weightMeasurement = measurement
            )
        }
    }

    fun onQuickRemove(
        measurementId: Long
    ) {
        viewModelScope.launch {
            addFoodRepository.removeMeasurement(measurementId)
        }
    }

    private var _holders: List<InnerProductMeasurementHolder> = mutableListOf()

    fun holder(
        productId: Long,
        measurementId: Long?
    ): ProductMeasurementHolder {
        // Check cache
        val cached = _holders.firstOrNull {
            it.productId == productId && it.measurementId == measurementId
        }

        if (cached != null) {
            return cached
        }

        // If measurement id is null create new holder
        if (measurementId == null) {
            // Create new holder
            val holder = InnerProductMeasurementHolder(
                productId = productId,
                initialMeasurementId = null
            )

            // Update cache
            _holders = _holders + holder

            return holder
        }

        // Check if there is already a holder for the same product without measurement id set. If so,
        // reuse it.
        // This is fine because if there is a product with measurement id set it means that holder
        // without measurement id won't be used anymore.

        val sameProductHolders = _holders.filter {
            it.productId == productId && it.measurementId == null
        }

        if (sameProductHolders.count() != 1) {
            // Create new holder
            val holder = InnerProductMeasurementHolder(
                productId = productId,
                initialMeasurementId = measurementId
            )

            // Update cache
            _holders = _holders + holder

            return holder
        }

        val holder = sameProductHolders.first()

        holder.measurementIdStateFlow.value = measurementId

        return holder
    }

    private inner class InnerProductMeasurementHolder(
        val productId: Long,
        initialMeasurementId: Long?
    ) : ProductMeasurementHolder {
        val measurementIdStateFlow = MutableStateFlow(initialMeasurementId)

        override val measurementId
            get() = measurementIdStateFlow.value

        @OptIn(ExperimentalCoroutinesApi::class)
        override val measurement = measurementIdStateFlow.flatMapLatest {
            if (it != null) {
                addFoodRepository.observeMeasurementById(it)
            } else {
                addFoodRepository.observeMeasurementByProductId(productId)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }
}
