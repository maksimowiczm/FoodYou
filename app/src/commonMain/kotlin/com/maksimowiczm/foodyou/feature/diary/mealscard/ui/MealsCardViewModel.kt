package com.maksimowiczm.foodyou.feature.diary.mealscard.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.data.StringFormatRepository
import com.maksimowiczm.foodyou.feature.diary.mealscard.domain.ObserveMealsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalTime

internal class MealsCardViewModel(
    observeMealsUseCase: ObserveMealsUseCase,
    private val stringFormatRepository: StringFormatRepository
) : ViewModel() {
    val meals = observeMealsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )

    fun formatTime(time: LocalTime) = stringFormatRepository.formatTime(time)
}
