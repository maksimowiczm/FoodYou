package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.NotificationChannel
import android.app.NotificationManager
import com.maksimowiczm.foodyou.core.data.FoodYouNotificationChannel
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.getString

internal object DataSyncProgressNotification : FoodYouNotificationChannel {
    override val channelId = "DATA_SYNC_PROGRESS_NOTIFICATION_CHANNEL_ID"

    override suspend fun getChannel() = NotificationChannel(
        channelId,
        getString(Res.string.notification_data_sync_progress_channel_name),
        NotificationManager.IMPORTANCE_LOW
    )
}

internal object DataSyncNotification : FoodYouNotificationChannel {
    override val channelId = "DATA_SYNC_NOTIFICATION_CHANNEL_ID"

    override suspend fun getChannel() = NotificationChannel(
        channelId,
        getString(Res.string.notification_data_sync_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )
}
