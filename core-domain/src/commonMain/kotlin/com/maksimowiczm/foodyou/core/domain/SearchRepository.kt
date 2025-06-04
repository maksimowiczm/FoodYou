package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.search.SearchLocalDataSource
import com.maksimowiczm.foodyou.core.model.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface SearchRepository {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>>
}

internal class SearchRepositoryImpl(private val searchLocalDataSource: SearchLocalDataSource) :
    SearchRepository {
    override fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>> =
        searchLocalDataSource.observeRecentQueries(limit).mapValues {
            val date = Instant
                .fromEpochSeconds(it.epochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault())

            SearchQuery(
                query = it.query,
                date = date
            )
        }
}
