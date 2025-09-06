package com.maksimowiczm.foodyou.business.food.infrastructure.room

import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.FoodSearchDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.SearchEntry
import com.maksimowiczm.foodyou.core.food.domain.entity.FoodSearchHistory
import com.maksimowiczm.foodyou.core.food.domain.repository.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.core.shared.search.SearchQuery
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomFoodSearchHistoryRepository(private val foodSearchDao: FoodSearchDao) :
    FoodSearchHistoryRepository {
    override fun observeHistory(limit: Int): Flow<List<FoodSearchHistory>> =
        foodSearchDao.observeRecentSearches(limit).mapValues { it.toModel() }

    override suspend fun insert(entry: FoodSearchHistory) {
        foodSearchDao.insertSearchEntry(entry.toEntity())
    }
}

@OptIn(ExperimentalTime::class)
private fun SearchEntry.toModel(): FoodSearchHistory =
    FoodSearchHistory(
        date =
            epochSeconds
                .let(Instant::fromEpochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        query = SearchQuery.Text(query),
    )

@OptIn(ExperimentalTime::class)
private fun FoodSearchHistory.toEntity(): SearchEntry =
    SearchEntry(
        epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
        query = query.query,
    )
