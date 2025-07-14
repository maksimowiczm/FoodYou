package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.food.data.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodSearchMapper
import com.maksimowiczm.foodyou.feature.food.preferences.UseOpenFoodFacts
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductCountUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductPagesUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class FoodSearchViewModel(
    database: FoodDatabase,
    dataStore: DataStore<Preferences>,
    private val foodSearchMapper: FoodSearchMapper,
    private val observeOpenFoodFactsProduct: ObserveOpenFoodFactsProductPagesUseCase,
    private val observeOpenFoodFactsProductCount: ObserveOpenFoodFactsProductCountUseCase
) : ViewModel() {
    private val foodDao = database.foodDao
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val localPages = searchQuery.flatMapLatest { query ->
        Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { foodDao.observeFood(query) }
        ).flow.map { data ->
            data.map(foodSearchMapper::toModel)
        }.cachedIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val openFoodFactsPages = useOpenFoodFacts.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest {
            observeOpenFoodFactsProduct(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val openFoodFactsProductCount = useOpenFoodFacts.observe().filter { it }.flatMapLatest {
        searchQuery.flatMapLatest {
            observeOpenFoodFactsProductCount(it)
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = 0,
        started = SharingStarted.WhileSubscribed(2_000)
    )
}
