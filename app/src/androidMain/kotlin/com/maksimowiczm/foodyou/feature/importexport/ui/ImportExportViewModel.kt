package com.maksimowiczm.foodyou.feature.importexport.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.importexport.data.BackupService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class ImportExportViewModel(
    private val backupService: BackupService,
    private val dateProvider: DateProvider
) : ViewModel() {

    fun getCurrentDateTime() = runBlocking { dateProvider.observeDateTime().first() }

    fun onImport(path: Uri) {
        backupService.importFoodProducts(path)
    }

    fun onExport(path: Uri) {
        backupService.exportFoodProducts(path)
    }
}
