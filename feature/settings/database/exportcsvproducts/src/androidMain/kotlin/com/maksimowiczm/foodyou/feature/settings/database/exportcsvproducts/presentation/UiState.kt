package com.maksimowiczm.foodyou.feature.settings.database.exportcsvproducts.presentation

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface UiState {

    @Immutable data object WaitingForFile : UiState

    @Immutable data class Error(val message: String?) : UiState

    @Immutable data class Exporting(val count: Int) : UiState

    @Immutable data class Exported(val count: Int) : UiState
}
