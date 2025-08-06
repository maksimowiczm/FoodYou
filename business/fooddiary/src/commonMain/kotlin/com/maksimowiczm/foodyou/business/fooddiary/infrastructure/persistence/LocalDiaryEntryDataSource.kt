package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry

internal interface LocalDiaryEntryDataSource {

    suspend fun insert(diaryEntry: DiaryEntry): Long

    suspend fun update(diaryEntry: DiaryEntry)

    suspend fun delete(diaryEntry: DiaryEntry)
}
