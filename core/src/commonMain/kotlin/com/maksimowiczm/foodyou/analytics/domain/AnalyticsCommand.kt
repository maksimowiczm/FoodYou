package com.maksimowiczm.foodyou.analytics.domain

import kotlin.time.Instant

sealed interface AnalyticsCommand {
    /**
     * Records an application launch
     *
     * @param versionName The version name of the application at the time of launch (e.g.
     *   `"1.4.2"`).
     */
    data class RecordAppLaunch(val versionName: String, val timestamp: Instant) : AnalyticsCommand
}
