package com.maksimowiczm.foodyou.search.infrastructure

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.search.domain.SearchResult
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(private val dao: SearchDao) : SearchRepository {
    private val mapper = SearchResultMapper()

    override fun search(
        query: SearchQuery,
        language: Language,
        excludedRecipeIds: Set<Uuid>,
    ): Flow<PagingData<SearchResult>> {
        val factory = {
            when (query) {
                is SearchQuery.Blank -> dao.getPagingSource(language.tag, excludedRecipeIds)
                is SearchQuery.Barcode ->
                    dao.getPagingSourceByBarcode(query.barcode, language.tag, excludedRecipeIds)

                is SearchQuery.NotBlank ->
                    dao.getPagingSourceByQuery(query.query, language.tag, excludedRecipeIds)
            }
        }

        return Pager(config = PagingConfig(pageSize = 100), pagingSourceFactory = factory)
            .flow
            .map { data -> data.map(mapper::toDomain) }
    }

    override fun count(
        query: SearchQuery,
        language: Language,
        excludedRecipeIds: Set<Uuid>,
    ): Flow<Int> =
        when (query) {
            is SearchQuery.Blank -> dao.observeCount(excludedRecipeIds)
            is SearchQuery.Barcode -> dao.observeCountByBarcode(query.barcode, excludedRecipeIds)

            is SearchQuery.NotBlank -> dao.observeCountByQuery(query.query, excludedRecipeIds)
        }

    override suspend fun save(searchResult: SearchResult) {
        dao.upsert(mapper.toEntity(searchResult))
    }

    override suspend fun deleteProduct(id: Uuid) {
        dao.deleteByProductId(id)
    }

    override suspend fun deleteRecipe(id: Uuid) {
        dao.deleteByRecipeId(id)
    }
}
