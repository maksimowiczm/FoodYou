package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.net.Uri
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
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ExportProductsWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters),
    KoinComponent {

    private val exportProductsUseCase: ExportProductsUseCase by inject()

    private val notificationManager by lazy { NotificationManagerCompat.from(context) }

    override suspend fun doWork(): Result {
        val notificationId = 1_000
        val uriString = inputData.getString("uri") ?: return Result.failure()
        val uri = uriString.toUri()

        setForeground(
            createForegroundInfo(
                max = null,
                progress = null,
                notificationId = notificationId
            )
        )

        val result = internalDoWork(
            notificationId = notificationId,
            uri = uri
        )

        val notification = if (result) {
            createSuccessNotification(uri)
        } else {
            createFailureNotification()
        }

        notificationManager.notifyIfAllowed(
            id = notificationId + 1,
            notification = notification
        )

        return if (result) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    /**
     * @return true if the work was successful, false otherwise
     */
    suspend fun internalDoWork(notificationId: Int, uri: Uri): Boolean {
        val resolver = applicationContext.contentResolver
        val outputStream = runCatching {
            resolver.openOutputStream(uri)
        }.getOrElse {
            Logger.e(TAG, it) { "Error opening output stream" }
            return false
        } ?: return false

        return try {
            exportProductsUseCase(outputStream).collectLatest {
                notificationManager.notifyIfAllowed(
                    id = notificationId,
                    notification = createProgressNotification(it.total, it.progress)
                )
            }

            true
        } catch (e: Exception) {
            Logger.e(TAG, e) { "Error exporting products" }
            false
        } finally {
            outputStream.close()
        }
    }

    private suspend fun createForegroundInfo(
        max: Int?,
        progress: Int?,
        notificationId: Int
    ): ForegroundInfo {
        val notification = createProgressNotification(max, progress)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    private suspend fun createProgressNotification(max: Int?, progress: Int?): Notification {
        val channelId = DataSyncProgressNotification.CHANNEL_ID
        notificationManager.createNotificationChannel(DataSyncProgressNotification.getChannel())

        return if (max == null || progress == null) {
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(Res.string.notification_exporting_products))
                .setOngoing(true)
                .build()
        } else {
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(Res.string.notification_exporting_products))
                .setOngoing(true)
                .setProgress(max, progress, false)
                .setContentText("$progress / $max")
                .build()
        }
    }

    private suspend fun createSuccessNotification(uri: Uri): Notification {
        val channelId = DataSyncNotification.CHANNEL_ID
        notificationManager.createNotificationChannel(DataSyncNotification.getChannel())
        val contentResolver = applicationContext.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "*/*"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent.createChooser(shareIntent, ""),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(Res.string.notification_exporting_products_success))
            .addAction(R.drawable.ic_share, getString(Res.string.action_share), pendingIntent)
            .setOngoing(false)
            .build()
    }

    private suspend fun createFailureNotification(): Notification {
        val channelId = DataSyncNotification.CHANNEL_ID
        notificationManager.createNotificationChannel(DataSyncNotification.getChannel())

        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(Res.string.notification_exporting_products_failure))
            .setOngoing(false)
            .setAutoCancel(true)
            .build()
    }

    private companion object {
        const val TAG = "ExportProductsWorker"
    }
}
