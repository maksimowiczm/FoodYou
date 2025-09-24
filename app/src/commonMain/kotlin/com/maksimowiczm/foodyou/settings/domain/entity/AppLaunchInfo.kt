package com.maksimowiczm.foodyou.settings.domain.entity

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferences
import kotlin.time.Instant

/**
 * @param firstLaunch The timestamp of the very first launch of the app, null if not recorded yet
 * @param firstLaunchCurrentVersion The version and timestamp of the first launch of the current app
 *   version, null if not recorded yet
 * @param launchesCount The total number of times the app has been launched
 */
data class AppLaunchInfo(
    val firstLaunch: Instant?,
    val firstLaunchCurrentVersion: Pair<String, Instant>?,
    val launchesCount: Int,
) : UserPreferences
