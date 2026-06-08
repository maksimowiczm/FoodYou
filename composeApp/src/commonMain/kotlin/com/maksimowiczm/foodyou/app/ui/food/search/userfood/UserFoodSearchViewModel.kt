package com.maksimowiczm.foodyou.app.ui.food.search.userfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UserFoodSearchViewModel(
    avoidCircularDependencyWith: UserRecipeIdentity?,
    private val repository: SearchRepository,
    private val compositionRepository: UserRecipeCompositionRepository,
    foodNameSelector: FoodNameSelector,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val excludedIds: Flow<Set<Uuid>> =
        avoidCircularDependencyWith?.let { identity ->
            compositionRepository.observeAncestors(identity).map { ancestors ->
                ancestors.map { it.id }.toSet() + identity.id
            }
        } ?: flowOf(emptySet())

    val pages =
        combine(searchQuery, foodNameSelector.observeLanguage(), excludedIds) {
                query,
                language,
                excludedIds ->
                repository.search(query, language, excludedIds)
            }
            .flatMapLatest { it }
            .cachedIn(viewModelScope)

    val count =
        combine(searchQuery, foodNameSelector.observeLanguage(), excludedIds) {
                query,
                language,
                excludedIds ->
                repository.count(query, language, excludedIds)
            }
            .flatMapLatest { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun search(query: SearchQuery) {
        viewModelScope.launch { searchQuery.emit(query) }
    }
}
