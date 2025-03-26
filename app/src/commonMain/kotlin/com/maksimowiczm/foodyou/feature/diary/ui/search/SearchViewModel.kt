package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class SearchViewModel(addFoodRepository: AddFoodRepository) : ViewModel() {
    val recentQueries: StateFlow<List<ProductQuery>> = addFoodRepository.observeProductQueries(
        limit = 20
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    protected val mutableSearchQuery =
        MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }
    val searchQuery = mutableSearchQuery.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = null
    )

    fun onSearch(query: String?) {
        viewModelScope.launch {
            mutableSearchQuery.emit(query?.takeIf { it.isNotBlank() })
        }
    }

    abstract val pages: Flow<PagingData<ProductWithMeasurement>>
    abstract fun onQuickAdd(productId: Long, measurement: WeightMeasurement)
    abstract fun onQuickRemove(measurementId: Long)
}
