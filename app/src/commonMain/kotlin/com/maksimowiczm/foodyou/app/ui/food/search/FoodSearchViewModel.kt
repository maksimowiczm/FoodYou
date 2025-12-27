package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.food.FoodSource
import com.maksimowiczm.foodyou.common.domain.search.searchQuery
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.food.domain.entity.FoodId
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchUseCase
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class FoodSearchViewModel(
    private val excludedRecipeId: FoodId.Recipe?,
    searchHistoryRepository: FoodSearchHistoryRepository,
    private val foodSearchRepository: FoodSearchRepository,
    private val foodSearchUseCase: FoodSearchUseCase,
    private val dateProvider: DateProvider,
) : ViewModel() {

    // Use shared flow to allow emitting same value multiple times
    private val searchQuery =
        MutableSharedFlow<String?>(replay = 1).apply { runBlocking { emit(null) } }

    private val filter = MutableStateFlow(FoodFilter())

    fun search(query: String?) {
        viewModelScope.launch { searchQuery.emit(query) }
    }

    fun changeSource(source: FoodFilter.Source) {
        filter.update { it.copy(source = source) }
    }

    private val recentFoodPages =
        searchQuery.flatMapLatest { query ->
            foodSearchUseCase.searchRecent(query, excludedRecipeId).cachedIn(viewModelScope)
        }
    private val recentFoodState =
        searchQuery
            .flatMapLatest { query ->
                foodSearchRepository.searchRecentFoodCount(
                    query = searchQuery(query),
                    now = dateProvider.now(),
                    excludedRecipeId = excludedRecipeId,
                )
            }
            .map { count ->
                FoodSourceUiState(
                    remoteEnabled = RemoteStatus.LocalOnly,
                    pages = recentFoodPages,
                    count = count,
                    alwaysShowFilter = true,
                )
            }

    private val yourFoodPages = observeFoodPages(FoodSource.Type.User).cachedIn(viewModelScope)
    private val yourFoodState =
        observeFoodCount(FoodSource.Type.User).map { count ->
            FoodSourceUiState(
                remoteEnabled = RemoteStatus.LocalOnly,
                pages = yourFoodPages,
                count = count,
                alwaysShowFilter = true,
            )
        }

<<<<<<< Updated upstream
    private val openFoodFactsPages =
        observeFoodPages(FoodSource.Type.OpenFoodFacts).cachedIn(viewModelScope)
    private val openFoodFactsState =
        combine(observeFoodCount(FoodSource.Type.OpenFoodFacts), foodPreferences) { count, prefs ->
            FoodSourceUiState(
                remoteEnabled = prefs.isOpenFoodFactsEnabled.toRemoteStatus(),
                pages = openFoodFactsPages,
                count = count,
            )
        }

    private val usdaPages = observeFoodPages(FoodSource.Type.USDA).cachedIn(viewModelScope)
    private val usdaState =
        combine(observeFoodCount(FoodSource.Type.USDA), foodPreferences) { count, prefs ->
            FoodSourceUiState(
                remoteEnabled = prefs.isUsdaEnabled.toRemoteStatus(),
                pages = usdaPages,
                count = count,
            )
        }

    private val swissPages =
        observeFoodPages(FoodSource.Type.SwissFoodCompositionDatabase).cachedIn(viewModelScope)
    private val swissState =
        observeFoodCount(FoodSource.Type.SwissFoodCompositionDatabase).map { count ->
            FoodSourceUiState(
                remoteEnabled = RemoteStatus.LocalOnly,
                pages = swissPages,
=======
    private val tbcaPages =
        observeFoodPages(FoodSource.Type.TBCA).cachedIn(viewModelScope)
    private val tbcaState =
        observeFoodCount(FoodSource.Type.TBCA).map { count ->
            FoodSourceUiState(
                remoteEnabled = RemoteStatus.LocalOnly,
                pages = tbcaPages,
>>>>>>> Stashed changes
                count = count,
            )
        }

<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    private fun observeFoodCount(source: FoodSource.Type) =
        searchQuery.flatMapLatest { query ->
            foodSearchRepository.searchFoodCount(
                query = searchQuery(query),
                source = source,
                excludedRecipeId = excludedRecipeId,
            )
        }

    private fun observeFoodPages(source: FoodSource.Type) =
        searchQuery.flatMapLatest { query ->
            foodSearchUseCase.search(query, source, excludedRecipeId)
        }

    private val searchHistory =
        searchHistoryRepository
            .observeHistory(limit = 10)
            .map { list -> list.map { it.query } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = emptyList(),
            )

    val uiState =
        combine(
                recentFoodState,
                yourFoodState,
<<<<<<< Updated upstream
                openFoodFactsState,
                usdaState,
                swissState,
=======
                tbcaState,
>>>>>>> Stashed changes
                filter,
                searchHistory,
            ) {
                recentFoodState,
                yourFoodState,
<<<<<<< Updated upstream
                openFoodFactsState,
                usdaState,
                swissState,
=======
                tbcaState,
>>>>>>> Stashed changes
                filter,
                searchHistory ->
                FoodSearchUiState(
                    sources =
                        mapOf(
                            FoodFilter.Source.Recent to recentFoodState,
                            FoodFilter.Source.YourFood to yourFoodState,
<<<<<<< Updated upstream
                            FoodFilter.Source.OpenFoodFacts to openFoodFactsState,
                            FoodFilter.Source.USDA to usdaState,
                            FoodFilter.Source.SwissFoodCompositionDatabase to swissState,
=======
                            FoodFilter.Source.TBCA to tbcaState,
>>>>>>> Stashed changes
                        ),
                    filter = filter,
                    recentSearches = searchHistory.map { it.query },
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue =
                    FoodSearchUiState(
                        sources = emptyMap(),
                        filter = FoodFilter(),
                        recentSearches = emptyList(),
                    ),
            )

    init {
        searchQuery
            .flatMapLatest { query ->
                if (query == null) {
                    return@flatMapLatest emptyFlow()
                }

                val switchFlow =
                    combine(filter, uiState) { currentFilter, uiState ->
                        if (
                            (currentFilter.source != FoodFilter.Source.Recent &&
                                currentFilter.source != FoodFilter.Source.YourFood &&
                                currentFilter.source != FoodFilter.Source.TBCA) ||
                                uiState.currentSourceCount.positive()
                        ) {
                            return@combine
                        }

                        val recentCount = uiState.sources[FoodFilter.Source.Recent]?.count
                        if (recentCount.positive()) {
                            changeSource(FoodFilter.Source.Recent)
                            return@combine
                        }

                        val yourFoodCount = uiState.sources[FoodFilter.Source.YourFood]?.count
                        if (yourFoodCount.positive()) {
                            changeSource(FoodFilter.Source.YourFood)
                            return@combine
                        }

                        val tbcaCount = uiState.sources[FoodFilter.Source.TBCA]?.count
                        if (tbcaCount.positive()) {
                            changeSource(FoodFilter.Source.TBCA)
                            return@combine
                        }
                    }

                val now = Clock.System.now().toEpochMilliseconds()
                val deadline = now + 100L
                switchFlow.takeWhile { Clock.System.now().toEpochMilliseconds() < deadline }
            }
            .launchIn(viewModelScope)
    }
}

@OptIn(ExperimentalContracts::class)
private fun Int?.positive(): Boolean {
    contract { returns(true) implies (this@positive != null) }

    return this != null && this > 0
}
