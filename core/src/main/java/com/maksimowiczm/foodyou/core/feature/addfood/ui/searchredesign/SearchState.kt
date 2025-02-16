package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

@Composable
fun rememberSearchState(
    measurements: List<Measurement> = emptyList(),
    productIds: Flow<PagingData<Long>>
): SearchState {
    return remember {
        SearchState(
            measurements = measurements,
            productIds = productIds
        )
    }
}

@Immutable
data class Measurement(
    val id: Long,
    val productId: Long
)

@Immutable
class SearchState(
    val measurements: List<Measurement>,
    val productIds: Flow<PagingData<Long>>
)
