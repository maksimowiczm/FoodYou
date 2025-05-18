package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.NotificationChannel
import android.app.NotificationManager
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.getString

internal object ExportNotification {
    const val CHANNEL_ID = "EXPORT_CHANNEL_ID"

    suspend fun getChannel() = NotificationChannel(
        CHANNEL_ID,
        getString(Res.string.notification_export_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )
}
