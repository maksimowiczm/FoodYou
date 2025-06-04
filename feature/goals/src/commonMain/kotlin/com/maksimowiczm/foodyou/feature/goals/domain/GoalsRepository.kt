package com.maksimowiczm.foodyou.feature.goals.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.domain.MealRepository
import com.maksimowiczm.foodyou.core.domain.MeasurementRepository
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.feature.goals.data.GoalsPreferences
import com.maksimowiczm.foodyou.feature.goals.model.DailyGoals
import com.maksimowiczm.foodyou.feature.goals.model.DiaryDay
import com.maksimowiczm.foodyou.feature.goals.model.defaultGoals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

internal class GoalsRepository(
    private val dataStore: DataStore<Preferences>,
    private val mealRepository: MealRepository,
    private val measurementRepository: MeasurementRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeDiaryDay(date: LocalDate): Flow<DiaryDay> {
        val foods = mealRepository.observeMeals().flatMapLatest { meals ->
            meals.map { meal ->
                measurementRepository.observeMeasurements(
                    mealId = meal.id,
                    date = date
                ).map { measurements ->
                    meal to measurements
                }
            }.combine { it.toList() }.map { it.toMap() }
        }

        return combine(
            observeDailyGoals(),
            foods
        ) { dailyGoals, foods ->
            DiaryDay(
                date = date,
                foods = foods,
                dailyGoals = dailyGoals
            )
        }
    }

    fun observeDailyGoals(): Flow<DailyGoals> {
        val nutrientGoal = combine(
            dataStore.observe(GoalsPreferences.proteinsGoal),
            dataStore.observe(GoalsPreferences.carbohydratesGoal),
            dataStore.observe(GoalsPreferences.fatsGoal)
        ) { arr ->
            if (arr.any { it == null }) {
                return@combine null
            }

            arr.map { it!! }
        }

        return combine(
            dataStore.observe(GoalsPreferences.caloriesGoal),
            nutrientGoal
        ) { calories, nutrients ->
            if (nutrients == null || calories == null) {
                return@combine defaultGoals()
            }

            val (proteins, carbohydrates, fats) = nutrients

            DailyGoals(
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats
            )
        }
    }

    suspend fun setDailyGoals(goals: DailyGoals) {
        dataStore.set(
            GoalsPreferences.caloriesGoal to goals.calories,
            GoalsPreferences.proteinsGoal to goals.proteins,
            GoalsPreferences.carbohydratesGoal to goals.carbohydrates,
            GoalsPreferences.fatsGoal to goals.fats
        )
    }
}
