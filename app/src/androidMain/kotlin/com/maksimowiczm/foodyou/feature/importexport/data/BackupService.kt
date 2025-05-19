package com.maksimowiczm.foodyou.feature.importexport.data

import android.content.Context
import android.net.Uri
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import androidx.work.workDataOf
import foodyou.app.generated.resources.*

internal class BackupService(private val context: Context) {

    private val workManager: WorkManager
        get() = WorkManager.getInstance(context)

    /**
     * @return true if the export was started successfully, false otherwise
     */
    suspend fun exportFoodProducts(path: Uri): Boolean {
        val uriString = workDataOf("uri" to path.toString())

        val request = OneTimeWorkRequestBuilder<ExportProductsWorker>()
            .setInputData(uriString)
            .build()

        val res = runCatching {
            workManager.enqueue(request).await()
        }

        return res.isSuccess
    }

    /**
     * @return true if the import was started successfully, false otherwise
     */
    suspend fun importFoodProducts(path: Uri): Boolean {
        val uriString = workDataOf("uri" to path.toString())

        val request = OneTimeWorkRequestBuilder<ImportProductsWorker>()
            .setInputData(uriString)
            .build()

        val res = runCatching {
            workManager.enqueue(request).await()
        }

        return res.isSuccess
    }
}
