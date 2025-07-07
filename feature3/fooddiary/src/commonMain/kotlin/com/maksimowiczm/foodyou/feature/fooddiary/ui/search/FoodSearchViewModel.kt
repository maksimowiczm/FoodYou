package com.maksimowiczm.foodyou.feature.fooddiary.ui.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductCountUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.openfoodfacts.domain.ObserveOpenFoodFactsProductPagesUseCase
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.UseOpenFoodFacts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class FoodSearchViewModel(
    mealId: Long,
    date: LocalDate,
    database: FoodDiaryDatabase,
    dataStore: DataStore<Preferences>,
    private val observeOpenFoodFactsProduct: ObserveOpenFoodFactsProductPagesUseCase,
    private val observeOpenFoodFactsProductCount: ObserveOpenFoodFactsProductCountUseCase
) : ViewModel() {

    private val foodDao = database.foodDao
    private val mealDao = database.mealDao
    private val useOpenFoodFacts = dataStore.userPreference<UseOpenFoodFacts>()

    val meal = mealDao.observeMealById(mealId).stateIn(
        scope = viewModelScope,
        initialValue = null,
        started = WhileSubscribed(2_000)
    )

    val date = MutableStateFlow(date).asStateFlow()

    private val searchQuery = MutableStateFlow<String?>(null)

    /**
     * Triggers a search for food items based on the provided query.
     *
     * @param query The search query string. If null, it will reset the search.
     */
    fun search(query: String?) {
        searchQuery.value = query
    }

    val localPages = Pager(
        config = PagingConfig(
            pageSize = 30,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { foodDao.observeFood() }
    ).flow.cachedIn(viewModelScope)

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
        started = WhileSubscribed(2_000)
    )
}
