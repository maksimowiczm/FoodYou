package com.maksimowiczm.foodyou.feature.meal.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.domain.MealRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.meal.data.MealPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

internal interface ObserveMealsUseCase {
    operator fun invoke(date: LocalDate): Flow<List<Meal>>
}

internal class ObserveMealsUseCaseImpl(
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository,
    private val dataStore: DataStore<Preferences>,
    private val dateProvider: DateProvider
) : ObserveMealsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(date: LocalDate): Flow<List<Meal>> {
        return mealRepository.observeMeals().flatMapLatest { meals ->
            if (meals.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            meals.map { meal ->
                measurementRepository.observeMeasurements(
                    mealId = meal.id,
                    date = date
                ).map { food ->
                    Meal(
                        id = meal.id,
                        name = meal.name,
                        from = meal.from,
                        to = meal.to,
                        rank = meal.rank,
                        food = food
                    )
                }
            }.combine { it.toList() }
        }.flatMapLatest { meals ->
            combine(
                dataStore.observe(MealPreferences.ignoreAllDayMeals).map { it ?: false },
                dataStore.observe(MealPreferences.timeBasedSorting).map { it ?: false },
                dateProvider.observeMinutes()
            ) { ignoreAllDayMeals, timeBased, time ->
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
        }
    }
}

private fun shouldShowMeal(meal: Meal, time: LocalTime, ignoreAllDayMeals: Boolean): Boolean =
    if (meal.isAllDay) {
        !ignoreAllDayMeals
    } else if (meal.to < meal.from) {
        val minuteBeforeMidnight = LocalTime(23, 59, 59)
        val midnight = LocalTime(0, 0, 0)
        meal.from <= time && time <= minuteBeforeMidnight || midnight <= time && time <= meal.to
    } else {
        meal.from <= time && time <= meal.to
    }
