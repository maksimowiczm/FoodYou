package com.maksimowiczm.foodyou.app.ui.food.search

import androidx.compose.runtime.*
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow

@Immutable
data class FoodSearchUiState(
    val sources: Map<FoodFilter.Source, FoodSourceUiState<FoodSearchUiModel>>,
    val filter: FoodFilter,
    val recentSearches: List<String>,
) {
    val currentSourceState: FoodSourceUiState<FoodSearchUiModel>?
        get() = sources[filter.source]

    val currentSourceCount: Int?
        get() = currentSourceState?.count
}

/**
 * @param pages Flow of paginated food search results.
 * @param count The number of total items available in the database.
 * @param alwaysShowFilter If true, the filter button will always be shown regardless of the count
 *   or remote status. If false, the filter button will only be shown if there are items to filter
 *   or if remote search is enabled.
 */
@Immutable
data class FoodSourceUiState<T : FoodSearchUiModel>(
    val pages: Flow<PagingData<T>>,
    val count: Int,
    private val alwaysShowFilter: Boolean = false,
) {
    val shouldShowFilter: Boolean
        @Composable get() = alwaysShowFilter || count > 0

    @Composable fun collectAsLazyPagingItems() = pages.collectAsLazyPagingItems()
}
