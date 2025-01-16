package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {
    fun getSelectedDate(): LocalDate

    suspend fun setSelectedDate(date: LocalDate)

    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>
}
