package com.maksimowiczm.foodyou.feature.fooddiary.ui.goals.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.fooddiary.domain.ObserveMealsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate

internal class GoalsViewModel(
    date: LocalDate,
    private val observeMealsUseCase: ObserveMealsUseCase
) : ViewModel() {

    private val dateState = MutableStateFlow(date)
    val date = dateState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val meals = dateState.flatMapLatest { date ->
        observeMealsUseCase(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(2_000),
        initialValue = null
    )
}
