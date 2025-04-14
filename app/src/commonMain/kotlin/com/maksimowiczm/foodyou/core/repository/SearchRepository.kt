package com.maksimowiczm.foodyou.core.repository

import com.maksimowiczm.foodyou.core.data.source.SearchLocalDataSource
import com.maksimowiczm.foodyou.core.ext.mapValues
import com.maksimowiczm.foodyou.core.model.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface SearchRepository {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>>
}

internal class SearchRepositoryImpl(private val searchDao: SearchLocalDataSource) :
    SearchRepository {

    override fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>> =
        searchDao.observeRecentQueries(limit).mapValues {
            val date = Instant
                .fromEpochSeconds(it.epochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault())

            SearchQuery(
                query = it.query,
                date = date
            )
        }
}
