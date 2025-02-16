package com.maksimowiczm.foodyou.core.feature.addfood.ui.searchredesign

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductIdWithWeightMeasurementId
import kotlinx.coroutines.flow.Flow

@Composable
fun rememberSearchState(
    measurements: List<ProductIdWithWeightMeasurementId> = emptyList(),
    productIds: Flow<PagingData<Long>>
): SearchState {
    return remember(
        measurements,
        productIds
    ) {
        SearchState(
            measurements = measurements,
            productIds = productIds
        )
    }
}

@Immutable
class SearchState(
    val measurements: List<ProductIdWithWeightMeasurementId>,
    val productIds: Flow<PagingData<Long>>
)
