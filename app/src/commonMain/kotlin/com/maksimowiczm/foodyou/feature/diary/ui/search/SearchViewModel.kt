package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class SearchViewModel<M : ProductWithMeasurement> : ViewModel() {
    abstract val recentQueries: StateFlow<List<ProductQuery>>
    abstract val searchQuery: StateFlow<String?>
    abstract val pages: Flow<PagingData<M>>

    abstract fun onSearch(query: String?)
    abstract fun onQuickAdd(model: M)
    abstract fun onQuickRemove(model: M)
}
