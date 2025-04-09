package com.maksimowiczm.foodyou.feature.openfoodfacts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.feature.openfoodfacts.data.OpenFoodFactsSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class OpenFoodFactsSearchHintViewModel(
    private val settingsRepository: OpenFoodFactsSettingsRepository
) : ViewModel() {
    val showSearchHint = settingsRepository.observeOpenFoodFactsShowSearchHint().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = runBlocking {
            settingsRepository.observeOpenFoodFactsShowSearchHint().first()
        }
    )

    fun onDontShowAgain() {
        viewModelScope.launch {
            settingsRepository.hideOpenFoodFactsSearchHint()
        }
    }
}
