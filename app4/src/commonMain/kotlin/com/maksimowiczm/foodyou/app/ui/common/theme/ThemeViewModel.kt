package com.maksimowiczm.foodyou.app.ui.common.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.device.domain.DeviceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeViewModel(deviceRepository: DeviceRepository) : ViewModel() {
    val themeSettings =
        deviceRepository
            .observe()
            .map { it.themeSettings }
            .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)

    val nutrientsColors =
        deviceRepository
            .observe()
            .map { it.nutrientsColors }
            .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = null)
}
