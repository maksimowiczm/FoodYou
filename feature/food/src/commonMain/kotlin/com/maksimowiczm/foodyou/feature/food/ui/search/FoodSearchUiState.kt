package com.maksimowiczm.foodyou.feature.food.ui.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.Flow

@Immutable
internal data class FoodSearchUiState(
    val sources: Map<FoodFilter.Source, FoodSourceUiState>,
    val filter: FoodFilter,
    val recentSearches: List<String>
) {
    val currentSourceState: FoodSourceUiState?
        get() = sources[filter.source]
}

enum class RemoteStatus {
    Enabled,
    Disabled,
    LocalOnly;

    companion object {
        fun Boolean.toRemoteStatus() = if (this) Enabled else Disabled
    }
}

@Immutable
internal data class FoodSourceUiState(
    /**
     * Indicates whether the source is enabled for remote search
     */
    val remoteEnabled: RemoteStatus,
    /**
     * Flow of paginated food search results
     */
    val pages: Flow<PagingData<FoodSearch>>,
    /**
     * The number of total items available in the database
     */
    val count: Int,
    // You can hard override the visibility of the filter button
    private val alwaysShowFilter: Boolean = false
) {
    val shouldShowFilter: Boolean
        @Composable get() = alwaysShowFilter || count > 0 || remoteEnabled == RemoteStatus.Enabled

    @Composable
    fun collectAsLazyPagingItems() = pages.collectAsLazyPagingItems()
}
