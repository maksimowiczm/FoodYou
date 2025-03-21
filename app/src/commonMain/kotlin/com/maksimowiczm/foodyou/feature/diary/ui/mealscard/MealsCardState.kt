package com.maksimowiczm.foodyou.feature.diary.ui.mealscard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import com.valentinilk.shimmer.Shimmer
import kotlinx.datetime.LocalTime

@Composable
fun rememberMealsCardState(
    timeBasedSorting: Boolean,
    includeAllDayMeals: Boolean,
    diaryDay: DiaryDay?,
    time: LocalTime,
    shimmer: Shimmer
) = remember(timeBasedSorting, diaryDay, time, shimmer) {
    MealsCardState(
        timeBasedSorting = timeBasedSorting,
        includeAllDayMeals = includeAllDayMeals,
        diaryDay = diaryDay,
        time = time,
        shimmer = shimmer
    )
}

@Stable
class MealsCardState(
    val timeBasedSorting: Boolean,
    val includeAllDayMeals: Boolean,
    val diaryDay: DiaryDay?,
    val time: LocalTime,
    val shimmer: Shimmer
) {
    val meals by derivedStateOf {
        // If time-based sorting is enabled, show meals that are currently happening first, sorted
        // by their rank and the rest of the meals sorted by their rank.
        diaryDay?.meals?.sortedBy {
            if (timeBasedSorting) {
                if (shouldShowMeal(it, time)) it.rank else 1_000_000 + it.rank
            } else {
                it.rank
            }
        }
    }

    /**
     * Determines whether a meal should be shown based on the current time.
     *
     * This function handles three cases:
     * 1. Meals that are all-day meals
     * 2. Meals that span across midnight (where end time is less than start time)
     * 3. Regular meals within the same day (where start time is less than end time)
     *
     * For meals spanning midnight (e.g., from 22:00 to 04:00), the function checks if the current time:
     * - Falls between the start time and midnight (23:59), OR
     * - Falls between midnight (00:00) and the end time
     *
     * For regular meals (e.g., from 12:00 to 15:00), it simply checks if the current time
     * falls within the start and end times.
     */
    private fun shouldShowMeal(meal: Meal, time: LocalTime): Boolean = if (meal.isAllDay) {
        includeAllDayMeals
    } else if (meal.to < meal.from) {
        val minuteBeforeMidnight = LocalTime(23, 59, 59)
        val midnight = LocalTime(0, 0, 0)
        meal.from <= time && time <= minuteBeforeMidnight || midnight <= time && time <= meal.to
    } else {
        meal.from <= time && time <= meal.to
    }
}
