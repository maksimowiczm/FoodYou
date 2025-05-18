package com.maksimowiczm.foodyou.feature.importexport.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.util.DateProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class ImportExportViewModel(private val dateProvider: DateProvider) : ViewModel() {

    fun getCurrentDateTime() = runBlocking { dateProvider.observeDateTime().first() }

    fun onImport(path: Uri) {
        Logger.d { path.toString() }
    }

    fun onExport(path: Uri) {
        Logger.d { path.toString() }
    }
}
