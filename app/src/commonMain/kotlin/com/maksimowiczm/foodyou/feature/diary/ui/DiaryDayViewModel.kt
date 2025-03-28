package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.DiaryDayRepository
import kotlinx.datetime.LocalDate

abstract class DiaryDayViewModel(private val diaryDayRepository: DiaryDayRepository) :
    ViewModel() {
    fun observeDiaryDay(date: LocalDate) = diaryDayRepository.observeDiaryDay(date)
}
