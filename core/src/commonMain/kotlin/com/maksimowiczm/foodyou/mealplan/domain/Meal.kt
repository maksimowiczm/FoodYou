package com.maksimowiczm.foodyou.mealplan.domain

import kotlin.math.abs
import kotlin.uuid.Uuid
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable data class MealId(val value: Uuid = Uuid.random())

@Serializable
data class Meal(
    val id: MealId = MealId(),
    val name: String,
    val timeWindow: TimeWindow,
) {
    init {
        require(name.isNotBlank()) { "Meal name must not be blank" }
    }

    @Serializable
    sealed interface TimeWindow {
        @Serializable data class Range(val start: LocalTime, val end: LocalTime) : TimeWindow

        @Serializable data object AllDay : TimeWindow
    }
}

private const val SECONDS_PER_DAY = 24 * 60 * 60

/**
 * Checks if the given [time] is within this range.
 *
 * This method handles ranges that wrap around midnight.
 *
 * @param time The time to check.
 * @return `true` if [time] is inside the range, `false` otherwise.
 */
fun Meal.TimeWindow.Range.isInside(time: LocalTime): Boolean {
    val nowSec = time.toSecondOfDay()
    val startSec = start.toSecondOfDay()
    val endSec = end.toSecondOfDay()

    // Range may wrap past midnight (e.g. 22:00..02:00)
    return if (startSec <= endSec) nowSec in startSec..endSec
    else nowSec >= startSec || nowSec <= endSec
}

/**
 * Calculates the shortest distance in seconds between [time] and this range.
 *
 * Returns 0 if [time] is inside the range.
 *
 * @param time The time to check.
 * @return The number of seconds to the nearest boundary of the range.
 */
fun Meal.TimeWindow.Range.distanceInSeconds(time: LocalTime): Int {
    if (isInside(time)) return 0

    val nowSec = time.toSecondOfDay()
    val startSec = start.toSecondOfDay()
    val endSec = end.toSecondOfDay()

    return minOf(circularDistance(nowSec, startSec), circularDistance(nowSec, endSec))
}

private fun circularDistance(a: Int, b: Int): Int {
    val diff = abs(a - b)
    return minOf(diff, SECONDS_PER_DAY - diff)
}
