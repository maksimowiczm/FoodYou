package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.NotificationManager
import com.maksimowiczm.foodyou.core.data.FoodYouNotificationChannel
import foodyou.app.generated.resources.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

internal object DataSyncProgressNotification : FoodYouNotificationChannel(
    channelId = "DATA_SYNC_PROGRESS_NOTIFICATION_CHANNEL_ID",
    name = runBlocking { getString(Res.string.notification_data_sync_progress_channel_name) },
    importance = NotificationManager.IMPORTANCE_LOW
)

internal object DataSyncNotification : FoodYouNotificationChannel(
    channelId = "DATA_SYNC_NOTIFICATION_CHANNEL_ID",
    name = runBlocking { getString(Res.string.notification_data_sync_channel_name) },
    importance = NotificationManager.IMPORTANCE_DEFAULT
)
