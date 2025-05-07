package com.maksimowiczm.foodyou.feature.meal.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.feature.meal.domain.ObserveMealsWithSummaryUseCase
import kotlinx.datetime.LocalDate

internal class MealsCardViewModel(
    private val observeMealsWithSummaryUseCase: ObserveMealsWithSummaryUseCase
) : ViewModel() {
    fun observeMeals(date: LocalDate) = observeMealsWithSummaryUseCase(
        date = date
    )
}
