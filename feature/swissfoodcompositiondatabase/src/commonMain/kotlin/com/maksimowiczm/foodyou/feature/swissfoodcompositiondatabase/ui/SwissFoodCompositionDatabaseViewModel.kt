package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui

import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SwissFoodCompositionDatabaseViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow<SwissFoodCompositionDatabaseUiState>(
        SwissFoodCompositionDatabaseUiState.LanguagePick
    )
    val uiState = _uiState.asStateFlow()

    fun onLanguageSelected(language: Language) = launch {
        var progress = 0f

        while (progress < 1f) {
            delay(100)
            progress += 0.1f

            _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(progress)
        }

        delay(1000)

        _uiState.value = SwissFoodCompositionDatabaseUiState.Finished
    }
}
