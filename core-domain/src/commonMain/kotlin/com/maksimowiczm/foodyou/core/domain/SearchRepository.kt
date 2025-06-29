package com.maksimowiczm.foodyou.core.domain

import com.maksimowiczm.foodyou.core.database.search.SearchLocalDataSource
import com.maksimowiczm.foodyou.core.model.SearchQuery
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface SearchRepository {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>>
}

@OptIn(ExperimentalTime::class)
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
