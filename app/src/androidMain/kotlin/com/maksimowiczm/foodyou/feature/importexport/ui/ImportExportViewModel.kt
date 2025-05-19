package com.maksimowiczm.foodyou.feature.importexport.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.ext.launch
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.importexport.data.BackupService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking

internal class ImportExportViewModel(
    private val backupService: BackupService,
    private val dateProvider: DateProvider
) : ViewModel() {

    private val _eventBus = Channel<ImportExportEvent>()
    val eventBus = _eventBus.receiveAsFlow()

    fun getCurrentDateTime() = runBlocking { dateProvider.observeDateTime().first() }

    fun onImport(path: Uri) = launch {
        val result = backupService.importFoodProducts(path)

        if (result) {
            _eventBus.send(ImportExportEvent.ImportStarted)
        } else {
            _eventBus.send(ImportExportEvent.ImportFailedToStart)
        }
    }

    fun onExport(path: Uri) = launch {
        val result = backupService.exportFoodProducts(path)

        if (result) {
            _eventBus.send(ImportExportEvent.ExportStarted)
        } else {
            _eventBus.send(ImportExportEvent.ExportFailedToStart)
        }
    }
}
