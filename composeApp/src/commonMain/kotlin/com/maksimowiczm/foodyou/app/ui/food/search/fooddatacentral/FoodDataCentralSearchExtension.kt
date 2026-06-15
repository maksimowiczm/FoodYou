package com.maksimowiczm.foodyou.app.ui.food.search.fooddatacentral

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.app.ui.food.search.SearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSearchParameters
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class FoodDataCentralSearchExtension(
    viewModel: SearchViewModel,
    settingsRepository: FoodDataCentralSettingsRepository,
    private val foodDataCentralService: FoodDataCentralService,
) : SearchExtension(viewModel) {
    private val dataTypes = MutableStateFlow<Set<FoodDataCentralSearchParameters.DataType>?>(null)

    private val parameters: SharedFlow<FoodDataCentralSearchParameters> =
        combine(viewModel.searchQuery, dataTypes) { query, dataTypes ->
                FoodDataCentralSearchParameters(
                    query = query,
                    orderBy = FoodDataCentralSearchParameters.OrderBy.NameAscending,
                    dataTypes = dataTypes,
                )
            }
            .shareIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                replay = 1,
            )

    val pages =
        parameters
            .flatMapLatest { foodDataCentralService.search(it, PAGE_SIZE) }
            .cachedIn(viewModel.viewModelScope)

    val count =
        parameters
            .flatMapLatest(foodDataCentralService::count)
            .stateIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val shouldShowFilter =
        combine(count, settingsRepository.observe().map { it.remoteEnabled }) { count, enabled ->
                enabled || (count != null && count > 0)
            }
            .stateIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = false,
            )

    fun dataTypes(types: Set<FoodDataCentralSearchParameters.DataType>) {
        viewModel.viewModelScope.launch { dataTypes.emit(types.takeIf { it.isNotEmpty() }) }
    }

    companion object {
        private const val PAGE_SIZE = 200
    }
}
