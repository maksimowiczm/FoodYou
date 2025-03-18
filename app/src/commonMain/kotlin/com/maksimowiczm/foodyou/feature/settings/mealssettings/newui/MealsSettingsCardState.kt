package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Stable
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.LocalTimeInput

@Stable
class MealsSettingsCardState(
    val nameInput: TextFieldState,
    val fromTimeInput: LocalTimeInput,
    val toTimeInput: LocalTimeInput
)
