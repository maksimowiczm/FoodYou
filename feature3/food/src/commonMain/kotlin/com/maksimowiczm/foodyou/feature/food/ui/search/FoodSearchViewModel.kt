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
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.food.preferences.UseUSDA
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapValues
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FoodSearchViewModel(
    private val excludedRecipeId: FoodId.Recipe?,
    foodDatabase: FoodDatabase,
    private val openFoodFactsRemoteDataSource: OpenFoodFactsRemoteDataSource,
    dataStore: DataStore<Preferences>,
    private val foodSearchMapper: FoodSearchMapper,
    private val openFoodFactsMapper: OpenFoodFactsProductMapper
) : ViewModel() {
    private val foodSearchDao = foodDatabase.foodSearchDao

    private val useOpenFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()
    private val useUSDA = dataStore.userPreference<UseUSDA>()

    private val searchQuery = MutableStateFlow<String?>(null)

    /**
     * Triggers a search for food items based on the provided query.
     *
     * @param query The search query string. If null, it will reset the search.
     */
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

    private val _source = MutableStateFlow(FoodSource.Type.User)
    val source = _source.asStateFlow()

    fun setSource(source: FoodSource.Type) {
        _source.value = source
    }

    val recentSearches = foodSearchDao
        .observeRecentSearches(10)
        .mapValues { it.query }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(2_000)
        )

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    val openFoodFactsPages = useOpenFoodFacts.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest { query ->
            val isBarcode = query?.all { it.isDigit() } ?: false

            val mediator = if (query != null) {
                OpenFoodFactsRemoteMediator<FoodSearch>(
                    remoteDataSource = openFoodFactsRemoteDataSource,
                    foodDatabase = foodDatabase,
                    query = query,
                    country = null,
                    isBarcode = isBarcode,
                    mapper = openFoodFactsMapper
                )
            } else {
                null
            }

            pager(
                query = query,
                source = FoodSource.Type.OpenFoodFacts,
                mediator = mediator
            )
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    val usdaPages = useUSDA.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest { query ->
            pager(
                query = query,
                source = FoodSource.Type.USDA
            )
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    val localPages = searchQuery.flatMapLatest { query ->
        pager(
            query = query,
            source = FoodSource.Type.User
        )
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = source.flatMapLatest {
        when (it) {
            FoodSource.Type.User -> localPages
            FoodSource.Type.OpenFoodFacts -> openFoodFactsPages
            FoodSource.Type.USDA -> usdaPages
        }.mapData(foodSearchMapper::toModel)
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val localFoodCount = searchQuery.flatMapLatest { query ->
        foodSearchDao.observeFoodCount(
            query = query,
            source = FoodSource.Type.User
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = SharingStarted.WhileSubscribed(2_000)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val openFoodFactsFoodCount = searchQuery.flatMapLatest { query ->
        foodSearchDao.observeFoodCount(
            query = query,
            source = FoodSource.Type.OpenFoodFacts
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = SharingStarted.WhileSubscribed(2_000)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val usdaFoodCount = searchQuery.flatMapLatest { query ->
        foodSearchDao.observeFoodCount(
            query = query,
            source = FoodSource.Type.USDA
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = SharingStarted.WhileSubscribed(2_000)
    )

    private fun FoodSearchDao.observeFoodCount(query: String?, source: FoodSource.Type): Flow<Int> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        return if (isBarcode) {
            observeFoodCountByBarcode(
                barcode = query,
                source = source
            )
        } else {
            observeFoodCountByQuery(
                query = query,
                source = source,
                excludedRecipeId = excludedRecipeId?.id
            )
        }
    }

    private fun FoodSearchDao.observeFood(
        query: String?,
        source: FoodSource.Type
    ): PagingSource<Int, FoodSearch> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        return if (isBarcode) {
            observeFoodByBarcode(
                barcode = query,
                source = source
            )
        } else {
            observeFoodByQuery(
                query = query,
                source = source,
                excludedRecipeId = excludedRecipeId?.id
            )
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun pager(
        query: String?,
        source: FoodSource.Type,
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
