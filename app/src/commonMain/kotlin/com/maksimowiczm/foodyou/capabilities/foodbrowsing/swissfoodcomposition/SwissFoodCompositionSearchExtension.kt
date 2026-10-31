package com.maksimowiczm.foodyou.capabilities.foodbrowsing.swissfoodcomposition

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchViewModel
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionRepository
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SwissFoodCompositionSearchExtension(
    viewModel: SearchViewModel,
    private val repository: SwissFoodCompositionRepository,
    foodNameSelector: FoodNameSelector,
) : SearchExtension(viewModel) {
    private val languageTag = foodNameSelector.observeLanguage().map { it.tag }

    val pages =
        combine(viewModel.searchQuery, languageTag) { query, language ->
                query to language
            }
            .distinctUntilChanged()
            .flatMapLatest { (query, language) ->
                repository.search(
                    query = query.query,
                    pageSize = PAGE_SIZE,
                    languageCode = language,
                )
            }
            .cachedIn(viewModel.viewModelScope)

    val count =
        viewModel.searchQuery
            .map { query -> query.query }
            .distinctUntilChanged()
            .flatMapLatest(repository::count)
            .stateIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2.seconds),
                initialValue = null,
            )

    private companion object {
        const val PAGE_SIZE = 200
    }
}
