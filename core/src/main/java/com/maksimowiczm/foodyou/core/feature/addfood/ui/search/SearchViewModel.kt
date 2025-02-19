package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.QueryResult
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class SearchViewModel(
    private val addFoodRepository: AddFoodRepository,
    private val productRepository: ProductRepository,
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

    private var _holders: MutableMap<HolderKey, InnerProductMeasurementHolder> = mutableMapOf()

    fun holder(
        key: HolderKey,
        measurementId: Long?
    ): ProductMeasurementHolder {
        val cached = _holders[key]

        if (cached != null) {
            return cached.also {
                // Update measurementId if it changed (e.g. after adding new measurement)
                if (it.measurementId != measurementId) {
                    Log.d(TAG, "Updating holder measurement for $key")
                    it.measurementIdFlow.value = measurementId
                }
            }
        }

        Log.d(TAG, "Creating new holder for $key")

        val holder = InnerProductMeasurementHolder(
            productId = key.productId,
            initialMeasurementId = measurementId
        )

        _holders[key] = holder

        return holder
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private inner class InnerProductMeasurementHolder(
        val productId: Long,
        initialMeasurementId: Long?
    ) : ProductMeasurementHolder {
        val measurementIdFlow = MutableStateFlow(initialMeasurementId)

        override val measurementId: Long?
            get() = measurementIdFlow.value

        private val measurementFlow = measurementIdFlow.flatMapLatest { id ->
            if (id == null) {
                addFoodRepository.observeMeasurementByProductId(productId)
            } else {
                addFoodRepository.observeMeasurementById(id)
            }
        }.map {
            it?.measurement
        }

        private val product = productRepository.observeProductById(productId)

        override val model: StateFlow<ProductWithWeightMeasurement?> = combine(
            product.filterNotNull(),
            measurementFlow.filterNotNull()
        ) { p, wm ->
            ProductWithWeightMeasurement(
                product = p,
                measurement = wm
            )
        }.stateIn(
            scope = viewModelScope,
            // Don't do it lazily because it will be a waste if user deletes measurement
            started = SharingStarted.WhileSubscribed(30_000L),
            initialValue = null
        )
    }

    private companion object {
        const val TAG = "SearchViewModel"
    }
}

data class HolderKey(
    val productId: Long,
    val rank: Float
)
