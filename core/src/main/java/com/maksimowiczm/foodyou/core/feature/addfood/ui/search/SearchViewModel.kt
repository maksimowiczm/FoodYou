package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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
                it.measurementIdFlow.value = measurementId
            }
        }

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
        val measurementFlow = MutableStateFlow<WeightMeasurement?>(null)

        override val measurementId: Long?
            get() = measurementIdFlow.value

        // Update measurement only when measurementId changes but not when it's null. This
        // helps UI to not do unexpected changes with the measurement.
        init {
            viewModelScope.launch {
                measurementIdFlow
                    .flatMapLatest { id ->
                        when {
                            id == null && measurementFlow.value == null ->
                                addFoodRepository.observeMeasurementByProductId(productId)

                            id != null -> addFoodRepository.observeMeasurementById(id)

                            else -> flowOf(null)
                        }
                    }
                    .filterNotNull()
                    .map { it.measurement }
                    .collectLatest { measurementFlow.value = it }
            }
        }

        val product = productRepository.observeProductById(productId)

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
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }
}

data class HolderKey(
    val productId: Long,
    val extraId: Int
)
