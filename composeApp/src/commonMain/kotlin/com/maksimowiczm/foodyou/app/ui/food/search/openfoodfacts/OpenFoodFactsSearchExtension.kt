package com.maksimowiczm.foodyou.app.ui.food.search.openfoodfacts

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.app.ui.food.search.SearchExtension
import com.maksimowiczm.foodyou.app.ui.food.search.SearchViewModel
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSearchParameters
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class OpenFoodFactsSearchExtension(
    viewModel: SearchViewModel,
    settingsRepository: OpenFoodFactsSettingsRepository,
    private val openFoodFactsService: OpenFoodFactsService,
) : SearchExtension(viewModel) {
    private val version =
        MutableStateFlow(OpenFoodFactsSearchParameters.OpenFoodFactsVersion.SearchALicious)

    private val parameters: SharedFlow<OpenFoodFactsSearchParameters> =
        combine(viewModel.searchQuery, version) { query, version ->
                OpenFoodFactsSearchParameters(
                    query = query,
                    orderBy = OpenFoodFactsSearchParameters.OrderBy.Relevance,
                    version = version,
                )
            }
            .shareIn(
                scope = viewModel.viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                replay = 1,
            )

    val pages =
        parameters
            .flatMapLatest { openFoodFactsService.search(it, PAGE_SIZE) }
            .cachedIn(viewModel.viewModelScope)

    val count =
        parameters
            .flatMapLatest(openFoodFactsService::count)
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

    fun version(version: OpenFoodFactsSearchParameters.OpenFoodFactsVersion) {
        viewModel.viewModelScope.launch { this@OpenFoodFactsSearchExtension.version.emit(version) }
    }

    companion object {
        private const val PAGE_SIZE = 200
    }
}
