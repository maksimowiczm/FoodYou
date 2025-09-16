package com.maksimowiczm.foodyou.feature.food.shared.presentation.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.maksimowiczm.foodyou.app.business.shared.domain.search.FoodSearch
import kotlinx.coroutines.flow.Flow

@Immutable
internal data class FoodSearchUiState(
    val sources: Map<FoodFilter.Source, FoodSourceUiState>,
    val filter: FoodFilter,
    val recentSearches: List<String>,
) {
    val currentSourceState: FoodSourceUiState?
        get() = sources[filter.source]

    val currentSourceCount: Int?
        get() = currentSourceState?.count
}

internal enum class RemoteStatus {
    Enabled,
    Disabled,
    LocalOnly;

    companion object {
        fun Boolean.toRemoteStatus() = if (this) Enabled else Disabled
    }
}

/**
 * @param remoteEnabled Indicates whether the source is enabled for remote search.
 * @param pages Flow of paginated food search results.
 * @param count The number of total items available in the database.
 * @param alwaysShowFilter If true, the filter button will always be shown regardless of the count
 *   or remote status. If false, the filter button will only be shown if there are items to filter
 *   or if remote search is enabled.
 */
@Immutable
internal data class FoodSourceUiState(
    val remoteEnabled: RemoteStatus,
    val pages: Flow<PagingData<FoodSearch>>,
    val count: Int,
    private val alwaysShowFilter: Boolean = false,
) {
    val shouldShowFilter: Boolean
        @Composable get() = alwaysShowFilter || count > 0 || remoteEnabled == RemoteStatus.Enabled

    @Composable fun collectAsLazyPagingItems() = pages.collectAsLazyPagingItems()
}
