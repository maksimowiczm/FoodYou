package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain.ImportSwissDatabaseUseCase
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.domain.Language
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SwissFoodCompositionDatabaseViewModel(
    private val importSwissDatabaseUseCase: ImportSwissDatabaseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SwissFoodCompositionDatabaseUiState>(
        SwissFoodCompositionDatabaseUiState.LanguagePick
    )
    val uiState = _uiState.asStateFlow()

    private val mutex = Mutex()

    fun import(languages: Set<Language>) {
        if (mutex.isLocked) {
            return
        }

        viewModelScope.launch {
            mutex.withLock {
                _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(0f)

                try {
                    importSwissDatabaseUseCase.importWithFeedback(languages).collectLatest {
                        val progress = (it / (languages.size * 1100f)).coerceIn(0f, 1f)
                        _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(progress)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Logger.e(TAG, e) {
                        "Error while importing Swiss Food Composition Database"
                    }
                }

                delay(200)
                _uiState.value = SwissFoodCompositionDatabaseUiState.Finished
            }
        }
    }

    private companion object {
        const val TAG = "SwissFoodCompositionDatabaseViewModel"
    }
}
