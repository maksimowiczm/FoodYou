package com.maksimowiczm.foodyou.business.fooddiary.domain

import com.maksimowiczm.foodyou.fooddiary.domain.entity.Meal
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun interface ObserveDiaryMealsUseCase {
    fun observe(date: LocalDate): Flow<List<DiaryMeal>>
}

@OptIn(ExperimentalCoroutinesApi::class)
internal class ObserveDiaryMealsUseCaseImpl(
    private val mealRepository: MealRepository,
    private val mealsPreferencesRepository: UserPreferencesRepository<MealsPreferences>,
    private val foodEntryRepository: FoodDiaryEntryRepository,
    private val manualEntryRepository: ManualDiaryEntryRepository,
    private val dateProvider: DateProvider,
) : ObserveDiaryMealsUseCase {
    override fun observe(date: LocalDate): Flow<List<DiaryMeal>> {
        return combine(
                mealRepository.observeMeals(),
                mealsPreferencesRepository.observe(),
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
                        combine(
                            manualEntryRepository.observeAll(mealId = meal.id, date = date),
                            foodEntryRepository.observeAll(mealId = meal.id, date = date),
                        ) { manualEntries, foodEntries ->
                            (manualEntries + foodEntries).sortedBy { it.name }
                        }
                    }

                combine(diaryEntries) { entries ->
                    entries.zip(meals) { entries, meal ->
                        DiaryMeal(meal = meal, entries = entries)
                    }
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
