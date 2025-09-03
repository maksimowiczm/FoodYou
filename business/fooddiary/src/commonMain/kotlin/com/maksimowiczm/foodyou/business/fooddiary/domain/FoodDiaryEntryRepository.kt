package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface FoodDiaryEntryRepository {
    fun observe(id: FoodDiaryEntryId): Flow<FoodDiaryEntry?>

    fun observeAll(mealId: Long, date: LocalDate): Flow<List<FoodDiaryEntry>>

    suspend fun insert(
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
        createdAt: LocalDateTime,
    ): FoodDiaryEntryId

    suspend fun update(entry: FoodDiaryEntry)

    suspend fun delete(id: FoodDiaryEntryId)
}
