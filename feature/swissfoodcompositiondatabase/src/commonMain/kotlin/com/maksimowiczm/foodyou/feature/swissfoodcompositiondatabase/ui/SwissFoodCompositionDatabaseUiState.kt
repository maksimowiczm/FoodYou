package com.maksimowiczm.foodyou.feature.swissfoodcompositiondatabase.ui

internal sealed interface SwissFoodCompositionDatabaseUiState {
    data object LanguagePick : SwissFoodCompositionDatabaseUiState
    data class Importing(val progress: Float) : SwissFoodCompositionDatabaseUiState
    data object Finished : SwissFoodCompositionDatabaseUiState
}
