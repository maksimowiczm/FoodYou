package com.maksimowiczm.foodyou.feature.diary.domain

import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

fun interface ObserveDiaryDayUseCase {
    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay>

    operator fun invoke(date: LocalDate): Flow<DiaryDay> = observeDiaryDay(date)
}
