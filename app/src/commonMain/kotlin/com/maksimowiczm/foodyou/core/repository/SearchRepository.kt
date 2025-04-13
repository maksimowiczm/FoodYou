package com.maksimowiczm.foodyou.core.repository

import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.search.SearchDao
import com.maksimowiczm.foodyou.core.model.SearchQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface SearchRepository {
    fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>>
}

internal class SearchRepositoryImpl(database: FoodYouDatabase) : SearchRepository {
    val searchDao: SearchDao = database.searchDao

    override fun observeRecentQueries(limit: Int): Flow<List<SearchQuery>> =
        searchDao.observeRecentQueries(limit).map { list ->
            list.map {
                val date = Instant
                    .fromEpochSeconds(it.epochSeconds)
                    .toLocalDateTime(TimeZone.currentSystemDefault())

                SearchQuery(
                    query = it.query,
                    date = date
                )
            }
        }
}
