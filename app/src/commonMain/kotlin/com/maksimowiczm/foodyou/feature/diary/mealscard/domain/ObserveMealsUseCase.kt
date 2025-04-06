package com.maksimowiczm.foodyou.feature.diary.mealscard.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.data.DateProvider
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.feature.diary.data.DiaryPreferences
import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal data class Meal(
    val id: Long,
    val from: LocalTime,
    val to: LocalTime,
    val name: String,
    val rank: Int,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val isEmpty: Boolean
) {
    val isAllDay: Boolean
        get() = from == to
}

/**
 * Use case for observing meals in meals card.
 */
internal interface ObserveMealsUseCase {
    operator fun invoke(): Flow<List<Meal>>
}

internal class ObserveMealsUseCaseImpl(
    private val mealRepository: MealRepository,
    private val dataStore: DataStore<Preferences>,
    private val dateProvider: DateProvider
) : ObserveMealsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke() = mealRepository.observeMeals().map { list ->
        list.map {
            Meal(
                id = it.id,
                from = it.from,
                to = it.to,
                name = it.name,
                rank = it.rank,
                // TODO
                calories = 0,
                proteins = 0,
                carbohydrates = 0,
                fats = 0,
                isEmpty = false
            )
        }
    }.flatMapLatest { meals ->
        combine(
            dataStore.observe(DiaryPreferences.includeAllDayMeals).map { it ?: false },
            dataStore.observe(DiaryPreferences.timeBasedSorting).map { it ?: false },
            dateProvider.observeMinutes()
        ) { includeAllDayMeals, timeBased, time ->

            meals.sortedBy { meal ->
                if (timeBased) {
                    if (shouldShowMeal(meal, time, includeAllDayMeals)) {
                        meal.rank
                    } else {
                        1_000_000 + meal.rank
                    }
                } else {
                    meal.rank
                }
            }
        }
    }

    private fun shouldShowMeal(meal: Meal, time: LocalTime, includeAllDayMeals: Boolean): Boolean =
        if (meal.isAllDay) {
            includeAllDayMeals
        } else if (meal.to < meal.from) {
            val minuteBeforeMidnight = LocalTime(23, 59, 59)
            val midnight = LocalTime(0, 0, 0)
            meal.from <= time && time <= minuteBeforeMidnight || midnight <= time && time <= meal.to
        } else {
            meal.from <= time && time <= meal.to
        }
}
