package com.maksimowiczm.foodyou.app.ui.database.tbca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.importexport.tbca.domain.ImportTBCAUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for TBCA import screen.
 *
 * Manages the import state and progress for Brazilian Food Composition Table data.
 */
class TBCAViewModel(
    private val importTBCAUseCase: ImportTBCAUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TBCAUiState>(TBCAUiState.Initial)
    val uiState: StateFlow<TBCAUiState> = _uiState.asStateFlow()

    /**
     * Starts importing TBCA foods into the database.
     */
    fun import() {
        if (_uiState.value is TBCAUiState.Importing) {
            return // Already importing
        }

        viewModelScope.launch {
            _uiState.value = TBCAUiState.Importing(progress = 0f)

            try {
                importTBCAUseCase.import().collect { count ->
                    // Update progress (estimated total: 5668 foods)
                    val progress = count.toFloat() / ESTIMATED_TOTAL_FOODS
                    _uiState.value = TBCAUiState.Importing(progress = progress.coerceIn(0f, 1f))
                }

                _uiState.value = TBCAUiState.Finished
            } catch (e: Exception) {
                _uiState.value = TBCAUiState.Error(message = e.message ?: "Unknown error occurred")
            }
        }
    }

    /**
     * Resets the UI state to initial (after error or finish).
     */
    fun reset() {
        _uiState.value = TBCAUiState.Initial
    }

    private companion object {
        const val ESTIMATED_TOTAL_FOODS = 5668f
    }
}

/**
 * UI State for TBCA import screen.
 */
sealed interface TBCAUiState {
    /**
     * Initial state - ready to import.
     */
    data object Initial : TBCAUiState

    /**
     * Currently importing foods.
     *
     * @param progress Import progress from 0.0 to 1.0
     */
    data class Importing(val progress: Float) : TBCAUiState

    /**
     * Import finished successfully.
     */
    data object Finished : TBCAUiState

    /**
     * Import failed with an error.
     *
     * @param message Error message to display
     */
    data class Error(val message: String) : TBCAUiState
}
