package com.maksimowiczm.foodyou.business.food.infrastructure.room

import com.maksimowiczm.foodyou.business.food.domain.SearchHistory
import com.maksimowiczm.foodyou.business.food.domain.SearchHistoryRepository
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.FoodSearchDao
import com.maksimowiczm.foodyou.business.shared.infrastructure.room.food.SearchEntry
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapValues
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

internal class RoomSearchHistoryRepository(private val foodSearchDao: FoodSearchDao) :
    SearchHistoryRepository {
    override fun observeSearchHistory(limit: Int): Flow<List<SearchHistory>> =
        foodSearchDao.observeRecentSearches(limit).mapValues { it.toModel() }

    override suspend fun insertSearchHistory(entry: SearchHistory) {
        foodSearchDao.insertSearchEntry(entry.toEntity())
    }
}

@OptIn(ExperimentalTime::class)
private fun SearchEntry.toModel(): SearchHistory =
    SearchHistory(
        date =
            epochSeconds
                .let(Instant::fromEpochSeconds)
                .toLocalDateTime(TimeZone.currentSystemDefault()),
        query = query,
    )

@OptIn(ExperimentalTime::class)
private fun SearchHistory.toEntity(): SearchEntry =
    SearchEntry(
        epochSeconds = date.toInstant(TimeZone.currentSystemDefault()).epochSeconds,
        query = query,
    )
