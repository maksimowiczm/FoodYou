package com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FoodDataCentralSearchViewModel(
    private val service: FoodDataCentralService,
    settingsRepository: FoodDataCentralSettingsRepository,
) : ViewModel() {
    private val searchQuery = MutableSharedFlow<SearchQuery>(replay = 1)

    private val dataTypes = MutableStateFlow<Set<FoodDataCentralSearchParameters.DataType>?>(null)

    private val searchParameters =
        combine(searchQuery.distinctUntilChanged(), dataTypes) { query, dataTypes ->
                FoodDataCentralSearchParameters(
                    query = query,
                    orderBy = FoodDataCentralSearchParameters.OrderBy.NameAscending,
                    dataTypes = dataTypes,
                )
            }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                replay = 1,
            )

    val pages =
        searchParameters.flatMapLatest { service.search(it, PAGE_SIZE) }.cachedIn(viewModelScope)

    val count =
        searchParameters
            .flatMapLatest(service::count)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    private val enabled = settingsRepository.observe().map { it.remoteEnabled }

    val shouldShowFilter =
        combine(count, enabled) { count, enabled -> enabled || (count != null && count > 0) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = false,
            )

    fun search(query: SearchQuery) {
        viewModelScope.launch { searchQuery.emit(query) }
    }

    fun dataTypes(types: Set<FoodDataCentralSearchParameters.DataType>) {
        viewModelScope.launch { dataTypes.emit(types.takeIf { it.isNotEmpty() }) }
    }

    private companion object {
        private const val PAGE_SIZE = 200
    }
}
