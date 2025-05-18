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
import com.maksimowiczm.foodyou.feature.importexport.domain.ExportProductsUseCase
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExportProductsWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters),
    KoinComponent {

    private val exportProductsUseCase: ExportProductsUseCase by inject()

    private val notificationManager by lazy { NotificationManagerCompat.from(context) }

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo(null, null))

        val result = internalDoWork()

        val notification = if (result) {
            createSuccessNotification()
        } else {
            createFailureNotification()
        }

        notificationManager.notifyIfAllowed(10_001, notification)

        return if (result) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    /**
     * @return true if the work was successful, false otherwise
     */
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
            exportProductsUseCase(inputStream).collect {
                setForeground(createForegroundInfo(it.total, it.progress))
            }

            true
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Error exporting products" }
            false
        } finally {
            inputStream.close()
        }
    }

    private suspend fun createForegroundInfo(
        max: Int?,
        progress: Int?,
        notificationId: Int = 10_000
    ): ForegroundInfo {
        val notification = createProgressNotification(max, progress)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private suspend fun createProgressNotification(max: Int?, progress: Int?): Notification {
        val channelId = ExportNotification.CHANNEL_ID
        val channel = ExportNotification.getChannel()
        notificationManager.createNotificationChannel(channel)

        return if (max == null || progress == null) {
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(Res.string.notification_exporting_products))
                .setOngoing(true)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(Res.string.notification_exporting_products))
                .setOngoing(true)
                .setProgress(max, progress, false)
                .setContentText("$progress / $max")
                .build()
        }
    }

    private suspend fun createSuccessNotification(): Notification {
        val channelId = ExportNotification.CHANNEL_ID
        val channel = ExportNotification.getChannel()
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(Res.string.notification_exporting_products_success))
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
    }

    private suspend fun createFailureNotification(): Notification {
        val channelId = ExportNotification.CHANNEL_ID
        val channel = ExportNotification.getChannel()
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(Res.string.notification_exporting_products_failure))
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
    }

    private companion object {
        const val TAG = "ExportProductsWorker"
    }
}
