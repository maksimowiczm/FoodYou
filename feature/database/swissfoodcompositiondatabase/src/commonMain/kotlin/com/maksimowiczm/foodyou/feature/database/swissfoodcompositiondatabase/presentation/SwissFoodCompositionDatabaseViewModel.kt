package com.maksimowiczm.foodyou.feature.database.swissfoodcompositiondatabase.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.business.food.domain.ImportSwissFoodCompositionDatabaseUseCase
import com.maksimowiczm.foodyou.business.food.domain.SwissFoodCompositionDatabaseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SwissFoodCompositionDatabaseViewModel(
    private val importSwissUseCase: ImportSwissFoodCompositionDatabaseUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<SwissFoodCompositionDatabaseUiState>(
            SwissFoodCompositionDatabaseUiState.LanguagePick
        )
    val uiState = _uiState.asStateFlow()

    private val mutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun import(languages: Set<SwissFoodCompositionDatabaseRepository.Language>) {
        if (mutex.isLocked) {
            return
        }

        val size = languages.sumOf { it.size }
        viewModelScope.launch {
            mutex.withLock {
                _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(0f)

                importSwissUseCase.import(languages).collectLatest { count ->
                    val progress = count.toFloat() / size
                    _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(progress)
                }

                delay(200)
                _uiState.value = SwissFoodCompositionDatabaseUiState.Finished
            }
        }
    }
}
