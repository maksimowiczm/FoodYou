package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.maksimowiczm.foodyou.account.application.ObservePrimaryAccountUseCase
import com.maksimowiczm.foodyou.account.domain.AccountManager
import com.maksimowiczm.foodyou.account.domain.observePrimaryProfile
import com.maksimowiczm.foodyou.common.domain.FoodNameSelector
import com.maksimowiczm.foodyou.food.application.ObserveFoodsUseCase
import com.maksimowiczm.foodyou.food.domain.FoodProductIdentity
import com.maksimowiczm.foodyou.food.domain.FoodProductRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.search.domain.FoodSearchPreferencesRepository
import com.maksimowiczm.foodyou.food.search.domain.SearchParameters
import com.maksimowiczm.foodyou.food.search.domain.SearchQuery
import com.maksimowiczm.foodyou.food.search.domain.SearchQueryParser
import com.maksimowiczm.foodyou.food.search.domain.SearchableFoodRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsRepository
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import com.maksimowiczm.foodyou.userfood.domain.UserFoodSearchParameters
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
import kotlinx.coroutines.flow.flowOf
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
    observePrimaryAccountUseCase: ObservePrimaryAccountUseCase,
    private val nameSelector: FoodNameSelector,
    private val observeFoodsUseCase: ObserveFoodsUseCase,
    private val userFoodRepository: UserFoodRepository,
    private val openFoodFactsRepository: OpenFoodFactsRepository,
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

    private val favoriteFoods =
        observePrimaryAccountUseCase
            .observe()
            .flatMapLatest { acc -> accountManager.observePrimaryProfile(acc) }
            .map { it.favoriteFoods }

    private val favoriteFoodState =
        combine(favoriteFoods, searchQuery) { list, query ->
                val loadStates =
                    LoadStates(
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true),
                        LoadState.NotLoading(true),
                    )

                if (list.isEmpty()) {
                    return@combine flowOf(
                        FoodSourceUiState(
                            pages =
                                flowOf(
                                    PagingData.empty(
                                        mediatorLoadStates = loadStates,
                                        sourceLoadStates = loadStates,
                                    )
                                ),
                            count = 0,
                            alwaysShowFilter = true,
                        )
                    )
                }

                val foods =
                    observeFoodsUseCase.observe(*list.toTypedArray()).map { list ->
                        list.mapNotNull {
                            when (it) {
                                is FoodProductRepository.FoodStatus.Available ->
                                    FoodSearchUiModel.Loaded(it.food)

                                is FoodProductRepository.FoodStatus.Loading ->
                                    FoodSearchUiModel.Loading(FoodIdentity.Other(it.identity))

                                is FoodProductRepository.FoodStatus.Error,
                                is FoodProductRepository.FoodStatus.NotFound -> null
                            }
                        }
                    }

                foods.map { list ->
                    val list =
                        when (query) {
                            SearchQuery.Blank -> list
                            is SearchQuery.Barcode ->
                                list.filter {
                                    when (it) {
                                        is FoodSearchUiModel.Loaded ->
                                            it.barcode?.value?.contains(query.barcode) ?: false

                                        is FoodSearchUiModel.Loading -> true
                                    }
                                }

                            is SearchQuery.FoodDataCentralUrl ->
                                list.filter {
                                    val identity = it.identity
                                    identity is FoodProductIdentity.FoodDataCentral &&
                                        identity.fdcId == query.fdcId
                                }

                            is SearchQuery.OpenFoodFactsUrl ->
                                list.filter {
                                    val identity = it.identity
                                    identity is OpenFoodFactsProductIdentity &&
                                        identity.barcode == query.barcode
                                }

                            is SearchQuery.Text ->
                                list.filter {
                                    when (it) {
                                        is FoodSearchUiModel.Loaded -> it.name.contains(query.query)
                                        is FoodSearchUiModel.Loading -> true
                                    }
                                }
                        }

                    val sorted = list.sortedWith(FoodSearchUiModel.comparator(nameSelector))

                    FoodSourceUiState(
                        pages =
                            flowOf(
                                    PagingData.from(
                                        data = sorted,
                                        sourceLoadStates = loadStates,
                                        mediatorLoadStates = loadStates,
                                    )
                                )
                                .cachedIn(viewModelScope),
                        count = list.size,
                        alwaysShowFilter = true,
                    )
                }
            }
            .flatMapLatest { it }

    private val localSearchParams =
        combine(
            searchQuery,
            accountManager.observePrimaryAccountId().filterNotNull(),
            accountManager.observePrimaryProfileId(),
        ) { query, accountId, profileId ->
            UserFoodSearchParameters(
                query = query,
                orderBy = UserFoodSearchParameters.OrderBy.NameAscending,
                accountId = accountId,
                profileId = profileId,
            )
        }

    private val localPages =
        localSearchParams
            .flatMapLatest { params ->
                userFoodRepository.search(parameters = params, pageSize = 30)
            }
            .map { data -> data.map { FoodSearchUiModel.Loaded(it) } }
            .cachedIn(viewModelScope)

    private val localState =
        localSearchParams.flatMapLatest { params ->
            userFoodRepository.count(params).map { count ->
                FoodSourceUiState(pages = localPages, count = count, alwaysShowFilter = true)
            }
        }

    private val openFoodFactsSearchParams =
        searchQuery.map { query ->
            OpenFoodFactsSearchParameters(
                query = query,
                orderBy = OpenFoodFactsSearchParameters.OrderBy.NameAscending,
            )
        }

    private val openFoodFactsPages =
        openFoodFactsSearchParams
            .flatMapLatest { params ->
                openFoodFactsRepository.search(parameters = params, pageSize = 30)
            }
            .map { data -> data.map { FoodSearchUiModel.Loaded(it) } }
            .cachedIn(viewModelScope)

    private val openFoodFactsState =
        combine(foodPreferences, openFoodFactsSearchParams) { prefs, params ->
                val count = openFoodFactsRepository.count(params)

                count.map { count ->
                    FoodSourceUiState(
                        alwaysShowFilter = prefs.allowOpenFoodFacts,
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
            .map { data -> data.map { FoodSearchUiModel.Loaded(it) } }
            .cachedIn(viewModelScope)

    private val usdaState =
        combine(foodPreferences, usdaSearchParams) { prefs, params ->
                val count = searchableFoodRepository.count(params)

                count.map { count ->
                    FoodSourceUiState(
                        alwaysShowFilter = prefs.allowFoodDataCentralUSDA,
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

    @Suppress("UNCHECKED_CAST")
    val uiState =
        combine(
                filter,
                searchHistory,
                localState,
                openFoodFactsState,
                usdaState,
                favoriteFoodState,
            ) { arr ->
                val filter = arr[0] as FoodFilter
                val searchHistory = arr[1] as List<SearchQuery.NotBlank>
                val localState = arr[2] as FoodSourceUiState<FoodSearchUiModel>
                val openFoodFactsState = arr[3] as FoodSourceUiState<FoodSearchUiModel>
                val usdaState = arr[4] as FoodSourceUiState<FoodSearchUiModel>
                val favoriteState = arr[5] as FoodSourceUiState<FoodSearchUiModel>

                FoodSearchUiState(
                    sources =
                        mapOf(
                            FoodFilter.Source.Favorite to favoriteState,
                            FoodFilter.Source.YourFood to localState,
                            FoodFilter.Source.OpenFoodFacts to openFoodFactsState,
                            FoodFilter.Source.USDA to usdaState,
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
            .filterIsInstance<SearchQuery.NotBlank>()
            .flatMapLatest {
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
