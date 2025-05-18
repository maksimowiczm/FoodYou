package com.maksimowiczm.foodyou.feature.importexport.data

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

internal class BackupService(private val context: Context) {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    fun exportFoodProducts(path: Uri) {
        val uriString = workDataOf("uri" to path.toString())

        val request = OneTimeWorkRequestBuilder<ExportProductsWorker>()
            .setInputData(uriString)
            .build()

        workManager.enqueue(request)

        Toast.makeText(
            context,
            runBlocking { getString(Res.string.neutral_export_started) },
            Toast.LENGTH_SHORT
        ).show()
    }

    fun importFoodProducts(path: Uri) {
        val uriString = workDataOf("uri" to path.toString())

        val request = OneTimeWorkRequestBuilder<ImportProductsWorker>()
            .setInputData(uriString)
            .build()

        workManager.enqueue(request)

        Toast.makeText(
            context,
            runBlocking { getString(Res.string.neutral_import_started) },
            Toast.LENGTH_SHORT
        ).show()
    }
}
