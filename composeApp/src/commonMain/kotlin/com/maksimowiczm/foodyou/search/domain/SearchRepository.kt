package com.maksimowiczm.foodyou.search.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(query: SearchQuery, language: Language): Flow<PagingData<SearchResult>>

    fun count(query: SearchQuery, language: Language): Flow<Int>

    suspend fun save(searchResult: SearchResult)

    suspend fun delete(identity: UserProductIdentity)
}
