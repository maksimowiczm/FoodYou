package com.maksimowiczm.foodyou.feature.diary.ui.mealssettings

import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.TextFieldValue

interface MealCardState {
    val nameInput: MutableState<TextFieldValue>
    val fromTimeInput: LocalTimeInput
    val toTimeInput: LocalTimeInput
    val isAllDay: MutableState<Boolean>
    val isDirty: Boolean
}
