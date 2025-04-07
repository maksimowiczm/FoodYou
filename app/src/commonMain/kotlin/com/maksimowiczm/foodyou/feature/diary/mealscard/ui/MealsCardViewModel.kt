package com.maksimowiczm.foodyou.feature.diary.mealscard.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.diary.mealscard.domain.ObserveMealsUseCase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal class MealsCardViewModel(
    private val observeMealsUseCase: ObserveMealsUseCase,
    private val stringFormatRepository: StringFormatRepository
) : ViewModel() {
    fun observeMeals(date: LocalDate) = observeMealsUseCase(
        date = date
    )

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)
}
