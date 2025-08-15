package com.maksimowiczm.foodyou.feature.food.shared.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.business.food.application.query.ObserveFoodPreferencesQuery
import com.maksimowiczm.foodyou.business.food.application.query.ObserveSearchHistoryQuery
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodCountQuery
import com.maksimowiczm.foodyou.business.food.application.query.SearchFoodQuery
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodCount
import com.maksimowiczm.foodyou.business.food.application.query.SearchRecentFoodQuery
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodSource
import com.maksimowiczm.foodyou.feature.food.shared.presentation.search.RemoteStatus.Companion.toRemoteStatus
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryBus
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
internal class FoodSearchViewModel(
    private val queryBus: QueryBus,
    private val excludedRecipeId: FoodId.Recipe?,
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

    private val foodPreferences =
        queryBus
            .dispatch(ObserveFoodPreferencesQuery)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue =
                    runBlocking { queryBus.dispatch(ObserveFoodPreferencesQuery).first() },
            )

    private val recentFoodPages =
        searchQuery.flatMapLatest { query ->
            queryBus
                .dispatch(SearchRecentFoodQuery(query = query, excludedRecipeId = excludedRecipeId))
                .cachedIn(viewModelScope)
        }
    private val recentFoodState =
        searchQuery
            .flatMapLatest { query -> queryBus.dispatch(SearchRecentFoodCount(query, null)) }
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
                count = count,
            )
        }

    private fun observeFoodCount(source: FoodSource.Type) =
        searchQuery.flatMapLatest { query ->
            queryBus.dispatch(
                SearchFoodCountQuery(
                    query = query,
                    source = source,
                    excludedRecipeId = excludedRecipeId,
                )
            )
        }

    private fun observeFoodPages(source: FoodSource.Type) =
        searchQuery.flatMapLatest { query ->
            queryBus.dispatch(
                SearchFoodQuery(query = query, source = source, excludedRecipeId = excludedRecipeId)
            )
        }

    private val searchHistory =
        queryBus
            .dispatch(ObserveSearchHistoryQuery)
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
                openFoodFactsState,
                usdaState,
                swissState,
                filter,
                searchHistory,
            ) {
                recentFoodState,
                yourFoodState,
                openFoodFactsState,
                usdaState,
                swissState,
                filter,
                searchHistory ->
                FoodSearchUiState(
                    sources =
                        mapOf(
                            FoodFilter.Source.Recent to recentFoodState,
                            FoodFilter.Source.YourFood to yourFoodState,
                            FoodFilter.Source.OpenFoodFacts to openFoodFactsState,
                            FoodFilter.Source.USDA to usdaState,
                            FoodFilter.Source.SwissFoodCompositionDatabase to swissState,
                        ),
                    filter = filter,
                    recentSearches = searchHistory,
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
                                currentFilter.source != FoodFilter.Source.YourFood) ||
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

                        val openFoodFactsCount =
                            uiState.sources[FoodFilter.Source.OpenFoodFacts]?.count
                        if (openFoodFactsCount.positive()) {
                            changeSource(FoodFilter.Source.OpenFoodFacts)
                            return@combine
                        }

                        val usdaCount = uiState.sources[FoodFilter.Source.USDA]?.count
                        if (usdaCount.positive()) {
                            changeSource(FoodFilter.Source.USDA)
                            return@combine
                        }
                    }

                val now = Clock.System.now().toEpochMilliseconds()
                switchFlow.takeWhile { Clock.System.now().toEpochMilliseconds() - now < 100L }
            }
            .launchIn(viewModelScope)
    }
}

@OptIn(ExperimentalContracts::class)
private fun Int?.positive(): Boolean {
    contract { returns(true) implies (this@positive != null) }

    return this != null && this > 0
}
