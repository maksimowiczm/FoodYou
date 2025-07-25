package com.maksimowiczm.foodyou.feature.food.ui.search2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapValues
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

internal class FoodSearchViewModel(excludedFood: FoodId.Recipe?, foodDatabase: FoodDatabase) :
    ViewModel() {

    private val foodSearchDao = foodDatabase.foodSearchDao

    private val _filter = MutableStateFlow(FoodFilter())
    val filter = _filter.asStateFlow()

    fun setSource(source: FoodFilter.Source) {
        _filter.update {
            it.copy(source = source)
        }
    }

    fun search(query: String?) {
        // TODO
    }

    val recentSearches = foodSearchDao
        .observeRecentSearches(10)
        .mapValues { it.query }
        .stateIn(
            scope = viewModelScope,
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(2_000)
        )
}
