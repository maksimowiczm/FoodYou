package com.maksimowiczm.foodyou.core.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.core.model.DailyGoals
import com.maksimowiczm.foodyou.core.model.defaultGoals
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface GoalsRepository {
    fun observeDailyGoals(): Flow<DailyGoals>

    suspend fun setDailyGoals(goals: DailyGoals)
}

private object GoalsPreferences {
    val caloriesGoal = intPreferencesKey("calories_goal")
    val proteinsGoal = floatPreferencesKey("proteins_goal")
    val carbohydratesGoal = floatPreferencesKey("carbohydrates_goal")
    val fatsGoal = floatPreferencesKey("fats_goal")
}

internal class GoalsRepositoryImpl(private val dataStore: DataStore<Preferences>) :
    GoalsRepository {
    override fun observeDailyGoals(): Flow<DailyGoals> {
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

    override suspend fun setDailyGoals(goals: DailyGoals) {
        dataStore.set(
            GoalsPreferences.caloriesGoal to goals.calories,
            GoalsPreferences.proteinsGoal to goals.proteins,
            GoalsPreferences.carbohydratesGoal to goals.carbohydrates,
            GoalsPreferences.fatsGoal to goals.fats
        )
    }
}
