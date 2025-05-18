package com.maksimowiczm.foodyou.feature.importexport.data

import android.app.NotificationChannel
import android.app.NotificationManager
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.getString

internal object ImportNotification {
    const val CHANNEL_ID = "IMPORT_CHANNEL_ID"

    suspend fun getChannel() = NotificationChannel(
        CHANNEL_ID,
        getString(Res.string.notification_import_channel_name),
        NotificationManager.IMPORTANCE_DEFAULT
    )
}
