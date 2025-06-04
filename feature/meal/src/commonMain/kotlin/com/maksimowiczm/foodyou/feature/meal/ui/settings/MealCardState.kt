package com.maksimowiczm.foodyou.feature.meal.ui.settings

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable

@Stable
internal interface MealCardState {
    val nameInput: TextFieldState
    val fromTimeInput: LocalTimeInput
    val toTimeInput: LocalTimeInput
    val isAllDay: MutableState<Boolean>
    val isDirty: Boolean
}
