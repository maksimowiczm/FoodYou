package com.maksimowiczm.foodyou.feature.importexport.data

import android.content.Context
import android.net.Uri
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

internal class BackupService(private val context: Context) {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    fun exportFoodProducts(path: Uri) {
        val uriString = workDataOf("uri" to path.toString())

        val request = OneTimeWorkRequestBuilder<ExportProductsWorker>()
            .setInputData(uriString)
            .build()

        workManager.enqueue(request)
    }

    fun importFoodProducts(path: Uri) {
        val uriString = workDataOf("uri" to path.toString())

        val request = OneTimeWorkRequestBuilder<ImportProductsWorker>()
            .setInputData(uriString)
            .build()

        workManager.enqueue(request)
    }
}
