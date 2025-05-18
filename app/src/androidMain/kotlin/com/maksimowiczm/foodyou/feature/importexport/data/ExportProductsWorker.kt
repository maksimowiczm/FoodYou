package com.maksimowiczm.foodyou.feature.importexport.data

import android.content.Context
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger

class ExportProductsWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val uriString = inputData.getString("uri") ?: return Result.failure()
        val uri = uriString.toUri()
        val resolver = applicationContext.contentResolver
        val inputStream = runCatching {
            resolver.openInputStream(uri)
        }.getOrElse {
            Logger.e(TAG, it) { "Error opening input stream" }
            return Result.failure()
        } ?: return Result.failure()

        return try {
            TODO()
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Error exporting products" }
            Result.failure()
        } finally {
            inputStream.close()
        }
    }

    private companion object {
        const val TAG = "ExportProductsWorker"
    }
}
