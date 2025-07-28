package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.ext.mapData
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearch
import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearchDao
import com.maksimowiczm.foodyou.feature.food.data.database.search.SearchEntry
import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsProductMapper
import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsRemoteMediator
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDAProductMapper
import com.maksimowiczm.foodyou.feature.food.data.network.usda.USDARemoteMediator
import com.maksimowiczm.foodyou.feature.food.domain.CreateProductUseCase
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.food.domain.RemoteProductMapper
import com.maksimowiczm.foodyou.feature.food.preferences.UsdaApiKey
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.food.preferences.UseUSDA
import com.maksimowiczm.foodyou.feature.food.ui.search.RemoteStatus.Companion.toRemoteStatus
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.usda.USDARemoteDataSource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapValues
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
internal class FoodSearchViewModel(
    dataStore: DataStore<Preferences>,
    foodDatabase: FoodDatabase,
    private val foodSearchMapper: FoodSearchMapper,
    private val remoteProductMapper: RemoteProductMapper,
    private val openFoodFactsMapper: OpenFoodFactsProductMapper,
    private val openFoodFactsRemoteDataSource: OpenFoodFactsRemoteDataSource,
    private val usdaRemoteDataSource: USDARemoteDataSource,
    private val usdaMapper: USDAProductMapper,
    private val createProductUseCase: CreateProductUseCase,
    private val productMapper: ProductMapper,
    private val excludedRecipeId: FoodId.Recipe?
) : ViewModel() {

    private val foodSearchDao = foodDatabase.foodSearchDao

    // Use shared flow to allow emitting same value multiple times
    private val searchQuery = MutableSharedFlow<String?>(replay = 1).apply {
        runBlocking { emit(null) }
    }

    private val filter = MutableStateFlow(FoodFilter())

    @OptIn(ExperimentalTime::class)
    fun search(query: String?) {
        viewModelScope.launch {
            searchQuery.emit(query)

            if (query == null) {
                return@launch
            }

            val isBarcode = query.all { it.isDigit() }
            if (isBarcode) {
                return@launch
            }

            foodSearchDao.upsertSearchEntry(
                SearchEntry(
                    query = query,
                    epochSeconds = Clock.System.now().epochSeconds
                )
            )
        }
    }

    fun changeSource(source: FoodFilter.Source) {
        viewModelScope.launch {
            filter.update {
                it.copy(
                    source = source
                )
            }
        }
    }

    private val useOpenFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()
    private val useUsda = dataStore.userPreference<UseUSDA>()
    private val usdaApiKey = dataStore.userPreference<UsdaApiKey>()

    private val recentFoodPages = searchQuery.flatMapLatest { query ->
        pager(query, FoodFilter.Source.Recent)
    }.cachedIn(viewModelScope)
    private val recentFoodState = searchQuery.flatMapLatest { query ->
        foodSearchDao.observeFoodCount(query, FoodFilter.Source.Recent).map { count ->
            FoodSourceUiState(
                remoteEnabled = RemoteStatus.LocalOnly,
                pages = recentFoodPages,
                count = count,
                alwaysShowFilter = true
            )
        }
    }

    private val yourFoodPages = searchQuery.flatMapLatest { query ->
        pager(query, FoodFilter.Source.YourFood)
    }.cachedIn(viewModelScope)
    private val yourFoodState = searchQuery.flatMapLatest { query ->
        foodSearchDao.observeFoodCount(query, FoodFilter.Source.YourFood).map { count ->
            FoodSourceUiState(
                remoteEnabled = RemoteStatus.LocalOnly,
                pages = yourFoodPages,
                count = count,
                alwaysShowFilter = true
            )
        }
    }

    private val openFoodFactsPages = combine(
        searchQuery,
        useOpenFoodFacts.observe()
    ) { query, enabled ->
        val mediator = if (enabled && query != null) {
            val isBarcode = query.all { it.isDigit() }

            OpenFoodFactsRemoteMediator<FoodSearch>(
                remoteDataSource = openFoodFactsRemoteDataSource,
                foodDatabase = foodDatabase,
                query = query,
                country = null,
                isBarcode = isBarcode,
                offMapper = openFoodFactsMapper,
                remoteMapper = remoteProductMapper,
                createProductUseCase = createProductUseCase,
                productMapper = productMapper
            )
        } else {
            null
        }

        pager(query, FoodFilter.Source.OpenFoodFacts, mediator)
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    private val openFoodFactsState = searchQuery.flatMapLatest { query ->
        combine(
            foodSearchDao.observeFoodCount(query, FoodFilter.Source.OpenFoodFacts),
            useOpenFoodFacts.observe()
        ) { count, enabled ->
            FoodSourceUiState(
                remoteEnabled = enabled.toRemoteStatus(),
                pages = openFoodFactsPages,
                count = count
            )
        }
    }

    private val usdaPages = combine(
        searchQuery,
        useUsda.observe(),
        usdaApiKey.observe()
    ) { query, enabled, apiKey ->
        val mediator = if (enabled && query != null) {
            USDARemoteMediator<FoodSearch>(
                remoteDataSource = usdaRemoteDataSource,
                foodDatabase = foodDatabase,
                query = query,
                apiKey = apiKey,
                usdaMapper = usdaMapper,
                remoteMapper = remoteProductMapper,
                createProductUseCase = createProductUseCase,
                productMapper = productMapper
            )
        } else {
            null
        }

        pager(query, FoodFilter.Source.USDA, mediator)
    }.flatMapLatest { it }.cachedIn(viewModelScope)
    private val usdaState = searchQuery.flatMapLatest { query ->
        combine(
            foodSearchDao.observeFoodCount(query, FoodFilter.Source.USDA),
            useUsda.observe()
        ) { count, enabled ->
            FoodSourceUiState(
                remoteEnabled = enabled.toRemoteStatus(),
                pages = usdaPages,
                count = count
            )
        }
    }

    private val swissFoodCompositionDatabasePages = searchQuery.flatMapLatest { query ->
        pager(query, FoodFilter.Source.SwissFoodCompositionDatabase)
    }.cachedIn(viewModelScope)
    private val swissFoodCompositionDatabaseState = searchQuery.flatMapLatest { query ->
        foodSearchDao.observeFoodCount(query, FoodFilter.Source.SwissFoodCompositionDatabase)
            .map { count ->
                FoodSourceUiState(
                    remoteEnabled = RemoteStatus.LocalOnly,
                    pages = swissFoodCompositionDatabasePages,
                    count = count
                )
            }
    }

    private val recentSearches = foodSearchDao
        .observeRecentSearches(10)
        .mapValues { it.query }

    val state = combine(
        recentFoodState,
        yourFoodState,
        openFoodFactsState,
        usdaState,
        swissFoodCompositionDatabaseState,
        filter,
        recentSearches
    ) {
            recent,
            yourFood,
            openFoodFacts,
            usda,
            swissFoodCompositionDatabase,
            filter,
            recentSearches
        ->
        FoodSearchUiState(
            sources = mapOf(
                FoodFilter.Source.Recent to recent,
                FoodFilter.Source.YourFood to yourFood,
                FoodFilter.Source.OpenFoodFacts to openFoodFacts,
                FoodFilter.Source.USDA to usda,
                FoodFilter.Source.SwissFoodCompositionDatabase to swissFoodCompositionDatabase
            ),
            filter = filter,
            recentSearches = recentSearches
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    private fun pager(
        query: String?,
        source: FoodFilter.Source,
        mediator: RemoteMediator<Int, FoodSearch>? = null
    ) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE
        ),
        remoteMediator = mediator,
        pagingSourceFactory = {
            foodSearchDao.observeFood(
                query = query,
                source = source
            )
        }
    ).flow.mapData(foodSearchMapper::toModel)

    private fun FoodSearchDao.observeFood(
        query: String?,
        source: FoodFilter.Source
    ): PagingSource<Int, FoodSearch> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        return if (source == FoodFilter.Source.Recent) {
            observeRecentFood()
        } else if (isBarcode) {
            observeFoodByBarcode(
                barcode = query,
                source = source.databaseSource
            )
        } else {
            observeFoodByQuery(
                query = query,
                source = source.databaseSource,
                excludedRecipeId = excludedRecipeId?.id
            )
        }
    }

    private fun FoodSearchDao.observeFoodCount(
        query: String?,
        source: FoodFilter.Source
    ): Flow<Int> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        return if (source == FoodFilter.Source.Recent) {
            observeRecentFoodCount()
        } else if (isBarcode) {
            observeFoodCountByBarcode(
                barcode = query,
                source = source.databaseSource
            )
        } else {
            observeFoodCountByQuery(
                query = query,
                source = source.databaseSource,
                excludedRecipeId = excludedRecipeId?.id
            )
        }
    }

    private companion object {
        const val PAGE_SIZE = 30
    }
}

private val FoodFilter.Source.databaseSource: FoodSource.Type?
    get() = when (this) {
        FoodFilter.Source.YourFood -> FoodSource.Type.User
        FoodFilter.Source.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
        FoodFilter.Source.USDA -> FoodSource.Type.USDA
        FoodFilter.Source.Recent -> null
        FoodFilter.Source.SwissFoodCompositionDatabase ->
            FoodSource.Type.SwissFoodCompositionDatabase
    }
