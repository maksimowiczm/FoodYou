package com.maksimowiczm.foodyou.core.data

import android.app.NotificationChannel
import androidx.core.app.NotificationManagerCompat

interface FoodYouNotificationChannel {
    val channelId: String

    suspend fun getChannel(): NotificationChannel
}

/**
 * Creates a notification channel with the given [FoodYouNotificationChannel].
 *
 * @return The ID of the created notification channel.
 */
suspend fun NotificationManagerCompat.createNotificationChannel(
    channel: FoodYouNotificationChannel
): String {
    val channel = channel.getChannel()
    createNotificationChannel(channel)
    return channel.id
}
