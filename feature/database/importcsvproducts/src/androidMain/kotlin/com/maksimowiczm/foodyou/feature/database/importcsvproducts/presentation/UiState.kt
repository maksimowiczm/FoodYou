package com.maksimowiczm.foodyou.feature.database.importcsvproducts.presentation

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface UiState {

    sealed interface WithError : UiState {
        val message: String?
    }

    @Immutable data object WaitingForFile : UiState

    @Immutable data class FileOpened(val header: List<String>) : UiState

    @Immutable data class FailedToOpenFile(override val message: String?) : WithError

    @Immutable data class MissingRequiredFields(override val message: String?) : WithError

    @Immutable data class Importing(val count: Int) : UiState

    @Immutable data class FailedToImport(override val message: String?) : WithError

    @Immutable data class ImportSuccess(val count: Int) : UiState
}
