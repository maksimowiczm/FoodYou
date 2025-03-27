package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.ui.search.SearchViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RecipeSearchViewModel(addFoodRepository: AddFoodRepository) : SearchViewModel() {
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

    override val pages: Flow<PagingData<ProductWithMeasurement>>
        get() = TODO("Not yet implemented")

    override fun onSearch(query: String?) {
        TODO("Not yet implemented")
    }

    override fun onQuickAdd(productId: Long, measurement: WeightMeasurement) {
        TODO("Not yet implemented")
    }

    override fun onQuickRemove(measurementId: Long) {
        TODO("Not yet implemented")
    }
}
