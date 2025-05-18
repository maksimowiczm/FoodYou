package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.NotificationChannel
import android.app.NotificationManager
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.getString

internal object DataSyncProgressNotification {
    const val CHANNEL_ID = "DATA_SYNC_PROGRESS_CHANNEL_ID"

    suspend fun getChannel() = NotificationChannel(
        CHANNEL_ID,
        getString(Res.string.notification_data_sync_progress_channel_name),
        NotificationManager.IMPORTANCE_LOW
    )
}

internal object DataSyncNotification {
    const val CHANNEL_ID = "DATA_SYNC_NOTIFICATION_CHANNEL_ID"

    suspend fun getChannel() = NotificationChannel(
        CHANNEL_ID,
        getString(Res.string.notification_data_sync_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )
}
