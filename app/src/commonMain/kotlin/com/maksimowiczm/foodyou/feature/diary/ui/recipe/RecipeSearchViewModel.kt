package com.maksimowiczm.foodyou.feature.diary.ui.recipe

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.diary.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchRecipeEntry
import com.maksimowiczm.foodyou.feature.diary.domain.QueryRecipeProductsUseCase
import com.maksimowiczm.foodyou.feature.diary.ui.search.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeSearchViewModel(
    addFoodRepository: AddFoodRepository,
    private val queryProducts: QueryRecipeProductsUseCase
) : SearchViewModel<SearchRecipeEntry>() {
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
        queryProducts(query, 0)
    }.cachedIn(viewModelScope)

    override fun onSearch(query: String?) {
        viewModelScope.launch {
            mutableSearchQuery.emit(query)
        }
    }

    override fun onQuickAdd(model: SearchRecipeEntry) {
        TODO("Not yet implemented")
    }

    override fun onQuickRemove(model: SearchRecipeEntry) {
        TODO("Not yet implemented")
    }
}
