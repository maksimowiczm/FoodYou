package com.maksimowiczm.foodyou.app.ui.common.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(deviceSettingsRepository: DeviceSettingsRepository) : ViewModel() {
    val themeSettings =
        deviceSettingsRepository
            .observe()
            .map { it.themeSettings }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val nutrientsColors =
        deviceSettingsRepository
            .observe()
            .map { it.nutrientsColors }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )
}
