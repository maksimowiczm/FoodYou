package com.maksimowiczm.foodyou.app.ui.food.search.userfood

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.app.ui.common.utility.FoodNameSelector
import com.maksimowiczm.foodyou.app.ui.food.search.SearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeCompositionRepository
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class UserFoodSearchExtension(
    viewModel: SearchViewModel,
    repository: SearchRepository,
    compositionRepository: UserRecipeCompositionRepository,
    foodNameSelector: FoodNameSelector,
) : SearchExtension(viewModel) {
    private val excludedIds: Flow<Set<Uuid>> =
        viewModel.avoidCircularDependencyWith?.let { identity ->
            compositionRepository.observeAncestors(identity).map { ancestors ->
                ancestors.map { it.id }.toSet() + identity.id
            }
        } ?: flowOf(emptySet())

    val pages =
        combine(viewModel.searchQuery, foodNameSelector.observeLanguage(), excludedIds) {
                query,
                language,
                excludedIds ->
                repository.search(query, language, excludedIds)
            }
            .flatMapLatest { it }
            .cachedIn(viewModel.viewModelScope)

    val count =
        combine(viewModel.searchQuery, foodNameSelector.observeLanguage(), excludedIds) {
                query,
                language,
                excludedIds ->
                repository.count(query, language, excludedIds)
            }
            .flatMapLatest { it }
            .stateIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}
