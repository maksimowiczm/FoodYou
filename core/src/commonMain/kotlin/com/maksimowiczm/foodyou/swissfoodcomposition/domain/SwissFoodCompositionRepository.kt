package com.maksimowiczm.foodyou.swissfoodcomposition.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface SwissFoodCompositionRepository {
    fun search(
        query: String?,
        pageSize: Int,
        languageCode: String,
    ): Flow<PagingData<SwissFoodCompositionProduct>>

    fun count(query: String?): Flow<Int>

    fun observe(id: SwissFoodCompositionProductId): Flow<SwissFoodCompositionProduct?>
}
