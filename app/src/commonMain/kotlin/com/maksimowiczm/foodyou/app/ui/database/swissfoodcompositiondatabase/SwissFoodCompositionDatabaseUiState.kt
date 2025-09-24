package com.maksimowiczm.foodyou.app.ui.database.swissfoodcompositiondatabase

internal sealed interface SwissFoodCompositionDatabaseUiState {
    data object LanguagePick : SwissFoodCompositionDatabaseUiState

    data class Importing(val progress: Float) : SwissFoodCompositionDatabaseUiState

    data object Finished : SwissFoodCompositionDatabaseUiState
}
