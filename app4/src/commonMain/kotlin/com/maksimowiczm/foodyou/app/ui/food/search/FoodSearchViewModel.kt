package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.app.ui.food.search.RemoteStatus.Companion.toRemoteStatus
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchQueryParser
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodRepository
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class FoodSearchViewModel(
    private val query: String?,
    //    private val excludedRecipeId: FoodId.Recipe?,
    private val foodSearchPreferencesRepository: FoodSearchPreferencesRepository,
    private val searchHistoryRepository: FoodSearchHistoryRepository,
    private val searchableFoodRepository: SearchableFoodRepository,
    private val clock: Clock,
    private val searchQueryParser: SearchQueryParser,
    private val accountManager: AccountManager,
) : ViewModel() {

    // Use shared flow to allow emitting same value multiple times
    private val searchQuery =
        MutableSharedFlow<SearchQuery>(replay = 1).apply {
            runBlocking { emit(searchQueryParser.parse(query)) }
        }

    private val filter = MutableStateFlow(FoodFilter())

    fun search(query: String?) {
        viewModelScope.launch {
            val parsedQuery = searchQueryParser.parse(query)
            searchQuery.emit(parsedQuery)
        }
    }

    fun changeSource(source: FoodFilter.Source) {
        filter.update { it.copy(source = source) }
    }

    private val foodPreferences =
        foodSearchPreferencesRepository
            .observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = runBlocking { foodSearchPreferencesRepository.observe().first() },
            )

    //    private val recentFoodPages =
    //        searchQuery.flatMapLatest { query ->
    //            foodSearchUseCase.searchRecent(query, excludedRecipeId).cachedIn(viewModelScope)
    //        }
    //    private val recentFoodState =
    //        searchQuery
    //            .flatMapLatest { query ->
    //                foodSearchRepository.searchRecentFoodCount(
    //                    query = searchQuery(query),
    //                    now = dateProvider.now(),
    //                    excludedRecipeId = excludedRecipeId,
    //                )
    //            }
    //            .map { count ->
    //                FoodSourceUiState(
    //                    remoteEnabled = RemoteStatus.LocalOnly,
    //                    pages = recentFoodPages,
    //                    count = count,
    //                    alwaysShowFilter = true,
    //                )
    //            }

    private val localSearchParams =
        combine(
            searchQuery,
            accountManager.observePrimaryAccountId().filterNotNull(),
            accountManager.observePrimaryProfileId(),
        ) { query, accountId, profileId ->
            SearchParameters.User(
                query = query,
                orderBy = SearchParameters.User.OrderBy.NameAscending,
                accountId = accountId,
                profileId = profileId,
            )
        }
    private val localPages =
        localSearchParams
            .flatMapLatest { params ->
                searchableFoodRepository.search(parameters = params, pageSize = 30)
            }
            .cachedIn(viewModelScope)
    private val localState =
        localSearchParams.flatMapLatest { params ->
            searchableFoodRepository.count(params).map { count ->
                FoodSourceUiState(
                    remoteEnabled = RemoteStatus.LocalOnly,
                    pages = localPages,
                    count = count,
                    alwaysShowFilter = true,
                )
            }
        }

    private val openFoodFactsSearchParams =
        searchQuery.map { query ->
            SearchParameters.OpenFoodFacts(
                query = query,
                orderBy = SearchParameters.OpenFoodFacts.OrderBy.NameAscending,
            )
        }

    private val openFoodFactsPages =
        openFoodFactsSearchParams
            .flatMapLatest { params ->
                searchableFoodRepository.search(parameters = params, pageSize = 30)
            }
            .cachedIn(viewModelScope)

    private val openFoodFactsState =
        combine(foodPreferences, openFoodFactsSearchParams) { prefs, params ->
                val count = searchableFoodRepository.count(params)

                count.map { count ->
                    FoodSourceUiState(
                        remoteEnabled = prefs.allowOpenFoodFacts.toRemoteStatus(),
                        pages = openFoodFactsPages,
                        count = count,
                    )
                }
            }
            .flatMapLatest { it }

    private val usdaSearchParams =
        searchQuery.map { query ->
            SearchParameters.FoodDataCentral(
                query = query,
                orderBy = SearchParameters.FoodDataCentral.OrderBy.NameAscending,
            )
        }

    private val usdaPages =
        usdaSearchParams
            .flatMapLatest { params ->
                searchableFoodRepository.search(parameters = params, pageSize = 30)
            }
            .cachedIn(viewModelScope)

    private val usdaState =
        combine(foodPreferences, usdaSearchParams) { prefs, params ->
                val count = searchableFoodRepository.count(params)

                count.map { count ->
                    FoodSourceUiState(
                        remoteEnabled = prefs.allowFoodDataCentralUSDA.toRemoteStatus(),
                        pages = usdaPages,
                        count = count,
                    )
                }
            }
            .flatMapLatest { it }

    private val searchHistory =
        accountManager.observePrimaryProfileId().flatMapLatest { profileId ->
            searchHistoryRepository
                .observe(profileId)
                .map { history -> history.history.map { it.query } }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(2_000),
                    initialValue = emptyList(),
                )
        }

    val uiState =
        combine(localState, openFoodFactsState, usdaState, filter, searchHistory) {
                localState,
                openFoodFactsState,
                usdaState,
                filter,
                searchHistory ->
                FoodSearchUiState(
                    sources =
                        mapOf(
                            //                        FoodFilter.Source.Recent to recentFoodState,
                            FoodFilter.Source.YourFood to localState,
                            FoodFilter.Source.OpenFoodFacts to openFoodFactsState,
                            FoodFilter.Source.USDA to usdaState,
                            //                        FoodFilter.Source.SwissFoodCompositionDatabase
                            // to swissState,
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
                        filter = FoodFilter(source = FoodFilter.Source.OpenFoodFacts),
                        recentSearches = emptyList(),
                    ),
            )

    init {
        searchQuery
            .filterIsInstance<SearchQuery.NotBlank>()
            .flatMapLatest { query ->
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
                val deadline = now + 100L
                switchFlow.takeWhile { Clock.System.now().toEpochMilliseconds() < deadline }
            }
            .launchIn(viewModelScope)

        searchQuery
            .filterIsInstance<SearchQuery.NotBlank>()
            .onEach {
                val profileId = accountManager.observePrimaryProfileId().first()
                val history = searchHistoryRepository.observe(profileId).first()
                history.recordSearchQuery(it, clock)
                searchHistoryRepository.save(history)
            }
            .launchIn(viewModelScope)

        searchQuery
            .onEach {
                when (it) {
                    is SearchQuery.FoodDataCentralUrl -> changeSource(FoodFilter.Source.USDA)
                    is SearchQuery.OpenFoodFactsUrl -> changeSource(FoodFilter.Source.OpenFoodFacts)
                    else -> Unit
                }
            }
            .launchIn(viewModelScope)
    }
}

@OptIn(ExperimentalContracts::class)
private fun Int?.positive(): Boolean {
    contract { returns(true) implies (this@positive != null) }

    return this != null && this > 0
}
