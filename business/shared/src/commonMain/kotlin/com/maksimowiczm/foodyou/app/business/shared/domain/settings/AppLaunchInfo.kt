package com.maksimowiczm.foodyou.app.business.shared.domain.settings

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * @param firstLaunch The timestamp of the very first launch of the app, null if not recorded yet
 * @param firstLaunchCurrentVersion The version and timestamp of the first launch of the current app
 *   version, null if not recorded yet
 * @param launchesCount The total number of times the app has been launched
 */
@OptIn(ExperimentalTime::class)
data class AppLaunchInfo(
    val firstLaunch: Instant?,
    val firstLaunchCurrentVersion: Pair<String, Instant>?,
    val launchesCount: Int,
) : UserPreferences
