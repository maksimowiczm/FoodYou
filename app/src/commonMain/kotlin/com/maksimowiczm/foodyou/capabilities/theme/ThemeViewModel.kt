package com.maksimowiczm.foodyou.capabilities.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.preferences.domain.ThemePreference
import com.maksimowiczm.foodyou.preferences.domain.UserPreferencesRepository
import com.maksimowiczm.foodyou.preferences.domain.observe
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(preferencesRepository: UserPreferencesRepository) : ViewModel() {
    val themeSettings =
        preferencesRepository
            .observe<ThemePreference>()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}
