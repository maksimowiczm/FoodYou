package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.ext.mapData
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.food.FoodSearch
import com.maksimowiczm.foodyou.feature.food.data.network.openfoodfacts.OpenFoodFactsRemoteMediator
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.network.OpenFoodFactsRemoteDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

internal class FoodSearchViewModel(
    foodDatabase: FoodDatabase,
    private val openFoodFactsRemoteDataSource: OpenFoodFactsRemoteDataSource,
    dataStore: DataStore<Preferences>,
    private val foodSearchMapper: FoodSearchMapper
) : ViewModel() {
    private val foodDao = foodDatabase.foodDao

    private val useOpenFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()

    private val searchQuery = MutableStateFlow<String?>(null)

    /**
     * Triggers a search for food items based on the provided query.
     *
     * @param query The search query string. If null, it will reset the search.
     */
    fun search(query: String?) {
        searchQuery.value = query
    }

    private val _source = MutableStateFlow(FoodSource.Type.User)
    val source = _source.asStateFlow()

    fun setSource(source: FoodSource.Type) {
        _source.value = source
    }

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
                    isBarcode = isBarcode
                )
            } else {
                null
            }

            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE
                ),
                pagingSourceFactory = {
                    if (isBarcode) {
                        foodDao.observeFoodByBarcode(
                            barcode = query,
                            source = FoodSource.Type.OpenFoodFacts
                        )
                    } else {
                        foodDao.observeFood(
                            query = query,
                            source = FoodSource.Type.OpenFoodFacts
                        )
                    }
                },
                remoteMediator = mediator
            ).flow
        }
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val localPages = searchQuery.flatMapLatest { query ->
        val isBarcode = query?.all { it.isDigit() } ?: false

        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                if (isBarcode) {
                    foodDao.observeFoodByBarcode(
                        barcode = query,
                        source = FoodSource.Type.User
                    )
                } else {
                    foodDao.observeFood(
                        query = query,
                        source = FoodSource.Type.User
                    )
                }
            }
        ).flow
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
    val pages = source.flatMapLatest {
        when (it) {
            FoodSource.Type.User -> localPages
            FoodSource.Type.OpenFoodFacts -> openFoodFactsPages
        }.mapData(foodSearchMapper::toModel)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val openFoodFactsProductCount = useOpenFoodFacts.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest { query ->
            val isBarcode = query?.all { it.isDigit() } ?: false

            if (isBarcode) {
                foodDao.observeFoodCountByBarcode(
                    barcode = query,
                    source = FoodSource.Type.OpenFoodFacts
                )
            } else {
                foodDao.observeFoodCountByQuery(
                    query = query,
                    source = FoodSource.Type.OpenFoodFacts
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = SharingStarted.WhileSubscribed(2_000)
    )

    private companion object {
        private const val PAGE_SIZE = 30
    }
}
