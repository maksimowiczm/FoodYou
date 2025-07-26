package com.maksimowiczm.foodyou.feature.food.ui.search2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.ext.mapData
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearch
import com.maksimowiczm.foodyou.feature.food.data.database.search.FoodSearchDao
import com.maksimowiczm.foodyou.feature.food.data.database.search.SearchEntry
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
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
    foodDatabase: FoodDatabase,
    private val foodSearchMapper: FoodSearchMapper
) : ViewModel() {

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

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = combine(filter, searchQuery) { filter, query ->
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                foodSearchDao.observeFood(
                    query = query,
                    source = filter.source
                )
            }
        ).flow.mapData(foodSearchMapper::toModel)
    }.flatMapLatest { it }.cachedIn(viewModelScope)

    private fun FoodSearchDao.observeFood(
        query: String?,
        source: FoodFilter.Source
    ): PagingSource<Int, FoodSearch> {
        val isBarcode = query?.all { it.isDigit() } ?: false

        return if (isBarcode) {
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

        return if (isBarcode) {
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

    private companion object {
        private const val PAGE_SIZE = 30
    }
}
