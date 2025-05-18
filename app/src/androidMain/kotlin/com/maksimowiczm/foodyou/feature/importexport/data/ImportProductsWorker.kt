package com.maksimowiczm.foodyou.feature.importexport.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ImportProductsWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}
