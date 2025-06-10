package com.maksimowiczm.foodyou.core.data

import android.app.NotificationChannel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat

abstract class FoodYouNotificationChannel(
    val channelId: String,
    val name: CharSequence,
    val importance: Int
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getChannel(): NotificationChannel = NotificationChannel(
        channelId,
        name,
        importance
    )
}

/**
 * Creates a notification channel with the given [FoodYouNotificationChannel].
 *
 * @return The ID of the created notification channel.
 */
fun NotificationManagerCompat.createNotificationChannel(
    channel: FoodYouNotificationChannel
): String {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return ""
    }

    val channel = channel.getChannel()
    createNotificationChannel(channel)
    return channel.id
}
