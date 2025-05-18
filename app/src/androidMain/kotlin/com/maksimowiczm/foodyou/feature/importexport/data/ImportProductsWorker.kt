package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.core.ext.notifyIfAllowed
import com.maksimowiczm.foodyou.feature.importexport.domain.ImportProductsUseCase
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ImportProductsWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters),
    KoinComponent {

    private val importProductsUseCase: ImportProductsUseCase by inject()

    private val notificationManager by lazy { NotificationManagerCompat.from(context) }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo(null))

        val result = internalDoWork()

        val notification = if (result) {
            createSuccessNotification()
        } else {
            createFailureNotification()
        }

        notificationManager.notifyIfAllowed(1_000, notification)

        return if (result) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    suspend fun internalDoWork(): Boolean {
        val uriString = inputData.getString("uri") ?: return false
        val uri = uriString.toUri()
        val resolver = applicationContext.contentResolver
        val inputStream = runCatching {
            resolver.openInputStream(uri)
        }.getOrElse {
            Logger.e(TAG, it) { "Error opening input stream" }
            return false
        } ?: return false

        return try {
            importProductsUseCase(inputStream).collect {
                setForeground(createForegroundInfo(it))
            }

            true
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Error exporting products" }
            false
        } finally {
            inputStream.close()
        }

        return false
    }

    private suspend fun createForegroundInfo(progress: Int?): ForegroundInfo {
        val notification = createProgressNotification(progress)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(1_000, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(1_000, notification)
        }
    }

    private suspend fun createProgressNotification(progress: Int?): Notification {
        val channelId = DataSyncProgressNotification.CHANNEL_ID
        val channel = DataSyncProgressNotification.getChannel()
        notificationManager.createNotificationChannel(channel)

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(Res.string.notification_importing_products))

        return if (progress == null) {
            builder.build()
        } else {
            builder
                .setOngoing(true)
                .setProgress(Int.MAX_VALUE, progress, true)
                .setContentText(progress.toString())
                .build()
        }
    }

    private suspend fun createSuccessNotification(): Notification {
        val channelId = DataSyncNotification.CHANNEL_ID
        val channel = DataSyncNotification.getChannel()
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(Res.string.notification_importing_products_success))
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
    }

    private suspend fun createFailureNotification(): Notification {
        val channelId = DataSyncNotification.CHANNEL_ID
        val channel = DataSyncNotification.getChannel()
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(Res.string.notification_importing_products_failure))
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
    }

    private companion object {
        private const val TAG = "ImportProductsWorker"
    }
}
