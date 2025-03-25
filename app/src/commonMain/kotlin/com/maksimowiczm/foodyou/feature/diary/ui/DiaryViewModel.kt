package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.diary.domain.ObserveDiaryDayUseCase
import kotlinx.datetime.LocalDate

abstract class DiaryViewModel(private val observeDiaryDayUseCase: ObserveDiaryDayUseCase) :
    ViewModel() {
    fun observeDiaryDay(date: LocalDate) = observeDiaryDayUseCase(date)
}
