package com.maksimowiczm.foodyou.feature.goals.ui.card

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.goals.data.DiaryDayRepository
import kotlinx.datetime.LocalDate

internal class CaloriesCardViewModel(private val diaryDayRepository: DiaryDayRepository) :
    ViewModel() {

    fun observeDiaryDay(date: LocalDate) = diaryDayRepository.observeDiaryDay(date)
}
