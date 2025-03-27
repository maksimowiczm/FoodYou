package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.MeasurementRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryEntry
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryEntrySuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.domain.QueryProductsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class MealDateSearchViewModel(
    addFoodRepository: AddFoodRepository,
    private val measurementRepository: MeasurementRepository,
    private val queryProductsUseCase: QueryProductsUseCase,
    val mealId: Long,
    val date: LocalDate
) : SearchViewModel() {
    override val recentQueries: StateFlow<List<ProductQuery>> =
        addFoodRepository.observeProductQueries(
            limit = 20
        ).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(30_000L),
            initialValue = emptyList()
        )

    private val mutableSearchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }

    override val searchQuery = mutableSearchQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pages = mutableSearchQuery.flatMapLatest { query ->
        queryProductsUseCase(
            mealId = mealId,
            date = date,
            query = query
        )
    }.cachedIn(viewModelScope)

    override fun onSearch(query: String?) {
        viewModelScope.launch {
            mutableSearchQuery.emit(query)
        }
    }

    override fun onQuickAdd(model: ProductWithMeasurement) {
        model as? DiaryEntrySuggestion ?: return

        viewModelScope.launch {
            measurementRepository.addMeasurement(
                date = date,
                mealId = mealId,
                productId = model.product.id,
                weightMeasurement = model.measurement
            )
        }
    }

    override fun onQuickRemove(model: ProductWithMeasurement) {
        model as? DiaryEntry ?: return

        viewModelScope.launch {
            measurementRepository.removeMeasurement(model.entryId)
        }
    }
}
