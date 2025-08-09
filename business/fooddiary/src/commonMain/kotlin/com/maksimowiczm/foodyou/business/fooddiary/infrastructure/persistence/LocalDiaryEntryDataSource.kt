package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

internal interface LocalDiaryEntryDataSource {

    fun observeEntries(mealId: Long, date: LocalDate): Flow<List<DiaryEntry>>

    fun observeEntry(entryId: Long): Flow<DiaryEntry?>

    suspend fun insert(diaryEntry: DiaryEntry): Long

    suspend fun update(diaryEntry: DiaryEntry)

    suspend fun delete(diaryEntry: DiaryEntry)
}
