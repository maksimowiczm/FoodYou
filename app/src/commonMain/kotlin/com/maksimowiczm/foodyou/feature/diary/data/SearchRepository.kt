package com.maksimowiczm.foodyou.feature.diary.data

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductQuery
import com.maksimowiczm.foodyou.feature.diary.data.model.SearchModel
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun observeProductQueries(limit: Int): Flow<List<ProductQuery>>

    fun queryProducts(query: String?): Flow<PagingData<SearchModel>>
}
