package com.maksimowiczm.foodyou.feature.meal.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.util.DateFormatter
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal class MealsCardViewModel(
    private val observeMealsWithSummaryUseCase: ObserveMealsWithSummaryUseCase,
    private val dateFormatter: DateFormatter
) : ViewModel() {
    fun observeMeals(date: LocalDate) = observeMealsWithSummaryUseCase(
        date = date
    )

    fun formatTime(time: LocalTime) = dateFormatter.formatTime(time)
}
