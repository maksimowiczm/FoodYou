package com.maksimowiczm.foodyou.feature.importexport.ui

sealed interface ImportExportEvent {
    data object ImportStarted : ImportExportEvent
    data object ImportFailedToStart : ImportExportEvent
    data object ExportStarted : ImportExportEvent
    data object ExportFailedToStart : ImportExportEvent
}
