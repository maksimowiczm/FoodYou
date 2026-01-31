package com.maksimowiczm.foodyou.foodsearch.infrastructure

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchHistory
import com.maksimowiczm.foodyou.foodsearch.domain.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.foodsearch.domain.SearchHistory
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQuery
import com.maksimowiczm.foodyou.foodsearch.domain.SearchQueryParser
import com.maksimowiczm.foodyou.foodsearch.infrastructure.room.SearchHistoryDao
import com.maksimowiczm.foodyou.foodsearch.infrastructure.room.SearchHistoryEntity
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FoodSearchHistoryRepositoryImpl(
    private val dao: SearchHistoryDao,
    private val searchQueryParser: SearchQueryParser,
) : FoodSearchHistoryRepository {
    override fun observe(profileId: ProfileId): Flow<FoodSearchHistory> {
        return dao.observeHistory(profileId.value, 10).map { list ->
            val history =
                list.mapNotNull {
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
