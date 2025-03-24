package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.data.DiaryRepository
import kotlinx.datetime.LocalDate

abstract class DiaryViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = diaryRepository.observeDiaryDay(date)
}
