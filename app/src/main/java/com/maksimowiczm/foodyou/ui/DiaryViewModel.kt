package com.maksimowiczm.foodyou.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.data.DiaryRepository
import kotlinx.datetime.LocalDate

open class DiaryViewModel(private val diaryRepository: DiaryRepository) : ViewModel() {
    fun observeDiaryDay(date: LocalDate) = diaryRepository.observeDiaryDay(date)
}
