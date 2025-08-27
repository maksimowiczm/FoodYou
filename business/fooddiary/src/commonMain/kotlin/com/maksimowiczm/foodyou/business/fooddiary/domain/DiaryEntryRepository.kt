package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DiaryEntryRepository {
    fun observeEntry(id: Long): Flow<DiaryEntry?>

    fun observeEntries(mealId: Long, date: LocalDate): Flow<List<DiaryEntry>>

    suspend fun insertDiaryEntry(
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
        createdAt: LocalDateTime,
    ): Long

    suspend fun updateDiaryEntry(entry: DiaryEntry)

    suspend fun deleteDiaryEntry(id: Long)
}
