package com.maksimowiczm.foodyou.search.infrastructure

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.maksimowiczm.foodyou.common.domain.Language
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchRepository
import com.maksimowiczm.foodyou.search.domain.SearchResult
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(database: SearchDatabase) : SearchRepository {
    private val mapper = SearchResultMapper()
    private val dao: SearchDao = database.searchDao

    override fun search(query: SearchQuery, language: Language): Flow<PagingData<SearchResult>> {
        val factory = {
            when (query) {
                is SearchQuery.Blank -> dao.getPagingSource(language.tag)
                is SearchQuery.Barcode -> dao.getPagingSourceByBarcode(query.barcode, language.tag)
                is SearchQuery.NotBlank -> dao.getPagingSourceByQuery(query.query, language.tag)
            }
        }

        return Pager(config = PagingConfig(pageSize = 100), pagingSourceFactory = factory)
            .flow
            .map { data -> data.map(mapper::toDomain) }
    }

    override fun count(query: SearchQuery, language: Language): Flow<Int> =
        when (query) {
            is SearchQuery.Blank -> dao.observeCount()
            is SearchQuery.Barcode -> dao.observeCountByBarcode(query.barcode)
            is SearchQuery.NotBlank -> dao.observeCountByQuery(query.query)
        }

    override suspend fun save(searchResult: SearchResult) {
        when (searchResult) {
            is SearchResult.UserProduct -> {
                dao.deleteByProductId(searchResult.identity.id)
                dao.insert(mapper.toEntity(searchResult))
            }
        }
    }

    override suspend fun delete(identity: UserProductIdentity) {
        dao.deleteByProductId(identity.id)
    }
}
