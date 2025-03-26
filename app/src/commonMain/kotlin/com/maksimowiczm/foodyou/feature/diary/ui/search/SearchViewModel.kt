package com.maksimowiczm.foodyou.feature.diary.ui.search

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

abstract class SearchViewModel : ViewModel() {
    abstract val recentQueries: StateFlow<List<ProductQuery>>
    abstract val searchQuery: StateFlow<String?>
    abstract val pages: Flow<PagingData<ProductWithMeasurement>>

    abstract fun onSearch(query: String?)
    abstract fun onQuickAdd(productId: Long, measurement: WeightMeasurement)
    abstract fun onQuickRemove(measurementId: Long)
}
