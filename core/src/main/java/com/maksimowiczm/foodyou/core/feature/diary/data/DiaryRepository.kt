package com.maksimowiczm.foodyou.core.feature.diary.data

import com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DiaryRepository {
    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>

    fun observeDailyGoals(): Flow<DailyGoals>

    suspend fun setDailyGoals(goals: DailyGoals)
}
