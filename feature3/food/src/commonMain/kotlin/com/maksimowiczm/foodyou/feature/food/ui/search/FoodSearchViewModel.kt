package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import com.maksimowiczm.foodyou.feature.usda.USDARemoteDataSource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapValues
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class FoodSearchViewModel(
    private val excludedRecipeId: FoodId.Recipe?,
    private val foodDatabase: FoodDatabase,
    private val foodSearchMapper: FoodSearchMapper,
    dataStore: DataStore<Preferences>,
    private val remoteProductMapper: RemoteProductMapper,
    private val openFoodFactsMapper: OpenFoodFactsProductMapper,
    private val openFoodFactsRemoteDataSource: OpenFoodFactsRemoteDataSource,
    private val usdaRemoteDataSource: USDARemoteDataSource,
    private val usdaMapper: USDAProductMapper,
    private val createProductUseCase: CreateProductUseCase,
    private val productMapper: ProductMapper
) : ViewModel() {

    private val openFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()
    private val usda = dataStore.userPreference<UseUSDA>()

    private val usdaApiKey = dataStore.userPreference<UsdaApiKey>()

    private val foodSearchDao = foodDatabase.foodSearchDao

    private val _filter = MutableStateFlow(FoodFilter())
    val filter = _filter.asStateFlow()

    fun setSource(source: FoodFilter.Source) {
        _filter.update {
            it.copy(source = source)
        }
    }

    private val searchQuery = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalTime::class)
    fun search(query: String?) {
        searchQuery.value = query

        viewModelScope.launch {
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

    val recentSearches = foodSearchDao
        .observeRecentSearches(10)
        .mapValues { it.query }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(2_000)
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun foodCount(source: FoodFilter.Source): StateFlow<Int> =
        searchQuery.flatMapLatest { query ->
            foodSearchDao.observeFoodCount(
                query = query,
                source = source
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = 0,
            started = SharingStarted.WhileSubscribed(2_000)
        )

    // ViewModel must store the counts
    val recentFoodCount = foodCount(FoodFilter.Source.Recent)
    val yourFoodCount = foodCount(FoodFilter.Source.YourFood)
    val openFoodFactsCount = foodCount(FoodFilter.Source.OpenFoodFacts)
    val usdaCount = foodCount(FoodFilter.Source.USDA)
    val swissCount =
        foodCount(FoodFilter.Source.SwissFoodCompositionDatabase)

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val openFoodFactsPages = combine(
        openFoodFacts.observe(),
        searchQuery
    ) { openFoodFactsEnabled, query ->
        val isBarcode = query?.all { it.isDigit() } ?: false

        val mediator = if (openFoodFactsEnabled && query != null) {
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

    @OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
    val usdaPages = combine(
        usda.observe(),
        usdaApiKey.observe(),
        searchQuery
    ) { usdaEnabled, usdaApiKey, query ->
        val mediator = if (usdaEnabled && query != null) {
            USDARemoteMediator<FoodSearch>(
                remoteDataSource = usdaRemoteDataSource,
                foodDatabase = foodDatabase,
                query = query,
                apiKey = usdaApiKey,
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

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    val pages = combine(
        filter,
        searchQuery
    ) { filter, query -> filter to query }.flatMapLatest { (filter, query) ->
        when (filter.source) {
            FoodFilter.Source.Recent,
            FoodFilter.Source.YourFood,
            FoodFilter.Source.SwissFoodCompositionDatabase -> pager(
                query = query,
                source = filter.source
            )

            FoodFilter.Source.OpenFoodFacts -> openFoodFactsPages
            FoodFilter.Source.USDA -> usdaPages
        }
    }.mapData(foodSearchMapper::toModel).cachedIn(viewModelScope)

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
                source = source.asDatabaseSource()
            )
        } else {
            observeFoodByQuery(
                query = query,
                source = source.asDatabaseSource(),
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
                source = source.asDatabaseSource()
            )
        } else {
            observeFoodCountByQuery(
                query = query,
                source = source.asDatabaseSource(),
                excludedRecipeId = excludedRecipeId?.id
            )
        }
    }

    private fun FoodFilter.Source.asDatabaseSource(): FoodSource.Type? = when (this) {
        FoodFilter.Source.YourFood -> FoodSource.Type.User
        FoodFilter.Source.OpenFoodFacts -> FoodSource.Type.OpenFoodFacts
        FoodFilter.Source.USDA -> FoodSource.Type.USDA
        FoodFilter.Source.Recent -> null
        FoodFilter.Source.SwissFoodCompositionDatabase ->
            FoodSource.Type.SwissFoodCompositionDatabase
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun pager(
        query: String?,
        source: FoodFilter.Source,
        mediator: RemoteMediator<Int, FoodSearch>? = null
    ): Flow<PagingData<FoodSearch>> = Pager(
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
    ).flow

    private companion object {
        private const val PAGE_SIZE = 30
    }
}
