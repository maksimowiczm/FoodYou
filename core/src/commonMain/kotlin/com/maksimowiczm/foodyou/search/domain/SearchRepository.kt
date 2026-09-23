package com.maksimowiczm.foodyou.search.domain

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(
        query: SearchQuery,
        language: Language,
        excludedRecipeIds: Set<Uuid>,
    ): Flow<PagingData<SearchResult>>

    fun count(query: SearchQuery, language: Language, excludedRecipeIds: Set<Uuid>): Flow<Int>

    suspend fun save(searchResult: SearchResult)

    suspend fun deleteProduct(id: Uuid)

    suspend fun deleteRecipe(id: Uuid)
}
