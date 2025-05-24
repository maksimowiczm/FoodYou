package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.repository.FoodRepository
import com.maksimowiczm.foodyou.core.domain.repository.MealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

internal class MeasurementViewModel(
    private val foodId: FoodId,
    private val foodRepository: FoodRepository,
    private val mealsRepository: MealRepository
) : ViewModel() {
    val food = foodRepository.observeFood(foodId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    val meals = mealsRepository.observeMeals().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = emptyList()
    )
}
