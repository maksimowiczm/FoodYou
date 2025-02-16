package com.maksimowiczm.foodyou.core.feature.addfood.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.navigation.AddFoodFeature
import com.maksimowiczm.foodyou.core.feature.diary.data.QueryResult
import com.maksimowiczm.foodyou.core.feature.product.data.ProductRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate

class SearchViewModel(
    private val diaryRepository: AddFoodRepository,
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

    var query: String? = null
    private var queryJob: Job? = null
    val queryState: MutableStateFlow<QueryResult<List<ProductWithWeightMeasurement>>> =
        MutableStateFlow(QueryResult.loading(emptyList()))

    init {
        onSearch(
            query = query,
            localOnly = true
        )
    }

    fun onBarcodeScan(barcode: String) = onSearch(
        query = barcode,
        localOnly = false
    )

    /**
     * Retry the last query. Can be used to refresh or initialize the data.
     */
    fun onRetry() {
        onSearch(
            query = query,
            localOnly = false
        )
    }

    fun onClearSearch() {
        onSearch(
            query = null,
            localOnly = true
        )
    }

    /**
     * Search for products.
     *
     * @param query The query to search for.
     * @param localOnly If true, only local data will be loaded.
     * @param persistError If true, the last error will be persisted.
     */
    fun onSearch(query: String?, localOnly: Boolean, persistError: Boolean = false) {
        this.query = query

        val error = queryState.value.error

        queryJob?.cancel()
        queryJob = viewModelScope.launch {
            diaryRepository.queryProducts(
                mealId = mealId,
                date = date,
                query = query?.trim()?.ifBlank { null },
                localOnly = localOnly
            ).transformWhile {
                val value = if (persistError) {
                    it.copy(
                        error = error
                    )
                } else {
                    it
                }

                emit(value)

                it.isLoading
            }.collectLatest { queryResult ->
                queryState.value = queryResult
            }
        }
    }

    val recentQueries = diaryRepository.observeProductQueries(
        limit = 20
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
        initialValue = emptyList()
    )

    suspend fun onQuickAdd(model: ProductWithWeightMeasurement): Long {
        return diaryRepository.addFood(
            date = date,
            mealId = mealId,
            productId = model.product.id,
            weightMeasurement = model.measurement
        )
    }

    fun onQuickRemove(model: ProductWithWeightMeasurement) {
        viewModelScope.launch {
            model.measurementId?.let { diaryRepository.removeFood(it) }
        }
    }

    val totalCalories = diaryRepository.observeTotalCalories(
        date = date,
        mealId = mealId
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
        initialValue = runBlocking {
            diaryRepository.observeTotalCalories(
                date = date,
                mealId = mealId
            ).first()
        }
    )

    fun onProductDelete(productId: Long) {
        viewModelScope.launch {
            productRepository.deleteProduct(productId)
            onSearch(
                query = query,
                localOnly = true,
                persistError = true
            )
        }
    }

    private companion object {
        // 30 seconds, because user often navigate up and down
        private const val STOP_TIMEOUT = 30_000L
    }
}
