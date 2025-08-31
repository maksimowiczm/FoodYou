package com.maksimowiczm.foodyou.feature.database.swissfoodcompositiondatabase.presentation

internal sealed interface SwissFoodCompositionDatabaseUiState {
    data object LanguagePick : SwissFoodCompositionDatabaseUiState

    data class Importing(val progress: Float) : SwissFoodCompositionDatabaseUiState

    data object Finished : SwissFoodCompositionDatabaseUiState
}
