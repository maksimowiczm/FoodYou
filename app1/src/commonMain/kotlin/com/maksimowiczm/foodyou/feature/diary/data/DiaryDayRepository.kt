package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface DiaryDayRepository {
    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>
}
