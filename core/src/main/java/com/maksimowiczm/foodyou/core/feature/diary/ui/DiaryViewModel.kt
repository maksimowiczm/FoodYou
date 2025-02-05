package com.maksimowiczm.foodyou.core.feature.diary.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.feature.diary.data.DiaryRepository
import java.time.LocalDate

open class DiaryViewModel(
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = diaryRepository.observeDiaryDay(date)
}
