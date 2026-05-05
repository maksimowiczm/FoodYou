package com.maksimowiczm.foodyou.search.infrastructure.history

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.search.domain.SearchQuery
import com.maksimowiczm.foodyou.search.domain.SearchQueryParser
import com.maksimowiczm.foodyou.search.domain.history.FoodSearchHistory
import com.maksimowiczm.foodyou.search.domain.history.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.search.domain.history.SearchHistory
import com.maksimowiczm.foodyou.search.infrastructure.SearchDatabase
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodSearchHistoryRepositoryImpl(
    database: SearchDatabase,
    private val searchQueryParser: SearchQueryParser,
) : FoodSearchHistoryRepository {
    private val dao: SearchHistoryDao = database.searchHistoryDao

    override fun observe(profileId: ProfileId): Flow<FoodSearchHistory> {
        return dao.observeHistory(profileId.value, 10).map { list ->
            val history = list.mapNotNull {
                val query =
                    searchQueryParser.parse(it.query) as? SearchQuery.NotBlank
                        ?: return@mapNotNull null

                SearchHistory(
                    query = query,
                    timestamp = Instant.fromEpochMilliseconds(it.timestampMillis),
                )
            }

            FoodSearchHistory.of(profileId, history)
        }
    }

    override suspend fun save(foodSearchHistory: FoodSearchHistory) {
        val entities =
            foodSearchHistory.history.map {
                SearchHistoryEntity(
                    profileId = foodSearchHistory.profileId.value,
                    query = it.query.query,
                    timestampMillis = it.timestamp.toEpochMilliseconds(),
                )
            }

        dao.upsert(entities)
    }
}
