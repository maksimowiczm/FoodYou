package com.maksimowiczm.foodyou.core.feature.diary.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryRepository
import kotlinx.datetime.LocalDate

open class DiaryViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = diaryRepository.observeDiaryDay(date)
}
