package com.maksimowiczm.foodyou.food.search.infrastructure.repository

import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.food.domain.entity.FoodSearchHistory
import com.maksimowiczm.foodyou.food.domain.repository.FoodSearchHistoryRepository
import com.maksimowiczm.foodyou.food.search.infrastructure.room.FoodSearchDao
import com.maksimowiczm.foodyou.food.search.infrastructure.room.SearchEntry
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class RoomFoodSearchHistoryRepository(private val foodSearchDao: FoodSearchDao) :
    FoodSearchHistoryRepository {
    override fun observeHistory(limit: Int): Flow<List<FoodSearchHistory>> =
        foodSearchDao.observeRecentSearches(limit).map { list -> list.map { it.toModel() } }

    override suspend fun insert(entry: FoodSearchHistory) {
        foodSearchDao.insertSearchEntry(entry.toEntity())
    }
}

private fun SearchEntry.toModel(): FoodSearchHistory =
    FoodSearchHistory(
        timestamp = epochSeconds.let(Instant::fromEpochSeconds),
        query = SearchQuery.Text(query),
    )

private fun FoodSearchHistory.toEntity(): SearchEntry =
    SearchEntry(epochSeconds = timestamp.epochSeconds, query = query.query)
