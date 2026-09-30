package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchExtension
import com.maksimowiczm.foodyou.capabilities.foodbrowsing.SearchViewModel
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UserFoodSearchExtension(
    viewModel: SearchViewModel,
    searchDao: SearchDao,
    compositionRepository: UserRecipeCompositionRepository,
    foodNameSelector: FoodNameSelector,
) : SearchExtension(viewModel) {
    private val excludedIds: Flow<Set<Uuid>> =
        viewModel.avoidCircularDependencyWith?.let { id ->
            compositionRepository.observeAncestors(id).map { ancestors ->
                ancestors.map { it.value }.toSet() + id.value
            }
        } ?: flowOf(emptySet())

    val pages =
        combine(
                viewModel.searchQuery,
                foodNameSelector.observeLanguage(),
                excludedIds,
            ) { query, language, excluded ->
                Triple(query, language, excluded)
            }
            .distinctUntilChanged()
            .flatMapLatest { (query, language, excluded) ->
                Pager(config = PagingConfig(pageSize = PAGE_SIZE)) {
                        when (query) {
                            is SearchQuery.Blank ->
                                searchDao.getPagingSource(language.tag, excluded)

                            is SearchQuery.Barcode ->
                                searchDao.getPagingSourceByBarcode(
                                    query.barcode,
                                    language.tag,
                                    excluded,
                                )

                            is SearchQuery.NotBlank ->
                                searchDao.getPagingSourceByQuery(
                                    query.query,
                                    language.tag,
                                    excluded,
                                )
                        }
                    }
                    .flow
            }
            .map { data -> data.map(SearchResultMapper::toDomain) }
            .cachedIn(viewModel.viewModelScope)

    val count =
        combine(viewModel.searchQuery, excludedIds) { query, excluded -> query to excluded }
            .distinctUntilChanged()
            .flatMapLatest { (query, excluded) ->
                when (query) {
                    is SearchQuery.Blank -> searchDao.observeCount(excluded)
                    is SearchQuery.Barcode ->
                        searchDao.observeCountByBarcode(query.barcode, excluded)
                    is SearchQuery.NotBlank -> searchDao.observeCountByQuery(query.query, excluded)
                }
            }
            .stateIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private companion object {
        const val PAGE_SIZE = 30
    }
}
