package com.maksimowiczm.foodyou.feature.garbage.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.garbage.domain.ObserveDiaryDayUseCase
import kotlinx.datetime.LocalDate

abstract class DiaryDayViewModel(private val observeDiaryDayUseCase: ObserveDiaryDayUseCase) :
    ViewModel() {
    fun observeDiaryDay(date: LocalDate) = observeDiaryDayUseCase(date)
}
