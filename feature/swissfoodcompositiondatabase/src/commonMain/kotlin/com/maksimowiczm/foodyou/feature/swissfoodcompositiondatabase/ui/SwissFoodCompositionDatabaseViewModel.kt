package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.feature.importexport.domain.csv.ImportProductsUseCase
import com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.SwissFoodCompositionDatabasePreferences
import foodyou.feature.swissfoodcompositiondatabase.generated.resources.Res
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SwissFoodCompositionDatabaseViewModel(
    private val importProductsUseCase: ImportProductsUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow<SwissFoodCompositionDatabaseUiState>(
        SwissFoodCompositionDatabaseUiState.LanguagePick
    )
    val uiState = _uiState.asStateFlow()

    private val mutex = Mutex()

    fun onLanguageSelected(language: Language) {
        if (mutex.isLocked) {
            return
        }

        viewModelScope.launch {
            mutex.withLock {
                val bytes = language.readBytes()
                val stream = bytes.inputStream()
                val max = 1100

                importProductsUseCase(stream).collectLatest {
                    val progress = it.toFloat() / max
                    _uiState.value = SwissFoodCompositionDatabaseUiState.Importing(progress)
                }

                dataStore.set(SwissFoodCompositionDatabasePreferences.showHint to false)

                delay(1000)

                _uiState.value = SwissFoodCompositionDatabaseUiState.Finished
            }
        }
    }
}

private suspend fun Language.readBytes(): ByteArray = when (this) {
    Language.ENGLISH -> Res.readBytes("files/swiss-food-composition-database/data.csv")
    Language.GERMAN -> Res.readBytes("files/swiss-food-composition-database/data-de-DE.csv")
    Language.FRENCH -> Res.readBytes("files/swiss-food-composition-database/data-fr-FR.csv")
    Language.ITALIAN -> Res.readBytes("files/swiss-food-composition-database/data-it-IT.csv")
}
