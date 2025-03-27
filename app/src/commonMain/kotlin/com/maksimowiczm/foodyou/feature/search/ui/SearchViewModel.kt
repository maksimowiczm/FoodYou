package com.maksimowiczm.foodyou.feature.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.feature.search.domain.ObserveProductQueriesUseCase
import com.maksimowiczm.foodyou.feature.search.domain.QueryProductsUseCase
import com.maksimowiczm.foodyou.feature.search.domain.model.Product
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val queryProductsUseCase: QueryProductsUseCase,
    observeProductQueriesUseCase: ObserveProductQueriesUseCase
) : ViewModel() {
    private val mutableSearchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }

    val recentQueries = observeProductQueriesUseCase.observeProductQueries(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages: Flow<PagingData<Product>> = mutableSearchQuery.flatMapLatest { query ->
        queryProductsUseCase.queryProducts(query)
    }.cachedIn(viewModelScope)

    fun onSearch(query: String?) {
        viewModelScope.launch {
            mutableSearchQuery.emit(query)
        }
    }
}
