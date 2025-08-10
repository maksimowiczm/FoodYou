package com.maksimowiczm.foodyou.business.fooddiary.application.query

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryMeal
import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class ObserveDiaryMealsQuery(val date: LocalDate) : Query<List<DiaryMeal>>

internal class ObserveDiaryMealsQueryHandler(
    private val localMeals: LocalMealDataSource,
    private val localDiaryEntry: LocalDiaryEntryDataSource,
    private val localMealsPreferences: LocalMealsPreferencesDataSource,
    private val dateProvider: DateProvider,
) : QueryHandler<ObserveDiaryMealsQuery, List<DiaryMeal>> {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun handle(query: ObserveDiaryMealsQuery): Flow<List<DiaryMeal>> =
        combine(
                localMeals.observeAllMeals(),
                localMealsPreferences.observe(),
                dateProvider.observeTime(),
            ) { meals, prefs, time ->
                val timeBased = prefs.useTimeBasedSorting
                val ignoreAllDayMeals = prefs.ignoreAllDayMeals

                meals.sortedBy { meal ->
                    if (timeBased) {
                        if (shouldShowMeal(meal, time, ignoreAllDayMeals)) {
                            meal.rank
                        } else {
                            1_000_000 + meal.rank
                        }
                    } else {
                        meal.rank
                    }
                }
            }
            .flatMapLatest { meals ->
                val diaryEntries =
                    meals.map { meal ->
                        localDiaryEntry.observeEntries(mealId = meal.id, date = query.date)
                    }

                combine(diaryEntries) { entries ->
                    entries.zip(meals) { entries, meal ->
                        DiaryMeal(meal = meal, entries = entries)
                    }
                }
            }
}

private fun shouldShowMeal(meal: Meal, time: LocalTime, ignoreAllDayMeals: Boolean): Boolean =
    with(meal) {
        if (from == to) {
            !ignoreAllDayMeals
        } else if (to < from) {
            val minuteBeforeMidnight = LocalTime(23, 59, 59)
            val midnight = LocalTime(0, 0, 0)
            from <= time && time <= minuteBeforeMidnight || midnight <= time && time <= to
        } else {
            from <= time && time <= to
        }
    }
