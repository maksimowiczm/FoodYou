package com.maksimowiczm.foodyou.app.infrastructure.opensource.goals

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.goals.domain.entity.DailyGoal
import com.maksimowiczm.foodyou.goals.domain.entity.MacronutrientGoal
import com.maksimowiczm.foodyou.goals.domain.entity.WeeklyGoals
import com.maksimowiczm.foodyou.goals.domain.repository.GoalsRepository
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFactsField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class DataStoreGoalsRepository(private val dataStore: DataStore<Preferences>) :
    GoalsRepository {
    override suspend fun updateWeeklyGoals(weeklyGoals: WeeklyGoals) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                val serialized = Json.encodeToString(DataStoreWeeklyGoals(weeklyGoals))
                set(GoalsDataStoreKeys.weeklyGoals, serialized)
            }
        }
    }

    override fun observeWeeklyGoals(): Flow<WeeklyGoals> =
        dataStore.data.map { preferences ->
            preferences[GoalsDataStoreKeys.weeklyGoals]?.let { serialized ->
                Json.decodeFromString<DataStoreWeeklyGoals>(serialized).toWeeklyGoals()
            } ?: WeeklyGoals.defaultGoals
        }

    override fun observeDailyGoals(date: LocalDate): Flow<DailyGoal> =
        observeWeeklyGoals().map {
            when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> it.monday
                DayOfWeek.TUESDAY -> it.tuesday
                DayOfWeek.WEDNESDAY -> it.wednesday
                DayOfWeek.THURSDAY -> it.thursday
                DayOfWeek.FRIDAY -> it.friday
                DayOfWeek.SATURDAY -> it.saturday
                DayOfWeek.SUNDAY -> it.sunday
            }
        }
}

private object GoalsDataStoreKeys {
    val weeklyGoals = stringPreferencesKey("fooddiary:weekly_goals_2")
}

@Serializable
private class DataStoreWeeklyGoals(
    val useSeparateGoals: Boolean,
    val monday: DataStoreDailyGoal,
    val tuesday: DataStoreDailyGoal,
    val wednesday: DataStoreDailyGoal,
    val thursday: DataStoreDailyGoal,
    val friday: DataStoreDailyGoal,
    val saturday: DataStoreDailyGoal,
    val sunday: DataStoreDailyGoal,
) {
    constructor(
        weeklyGoals: WeeklyGoals
    ) : this(
        useSeparateGoals = weeklyGoals.useSeparateGoals,
        monday = weeklyGoals.monday.intoDataStoreDailyGoal(),
        tuesday = weeklyGoals.tuesday.intoDataStoreDailyGoal(),
        wednesday = weeklyGoals.wednesday.intoDataStoreDailyGoal(),
        thursday = weeklyGoals.thursday.intoDataStoreDailyGoal(),
        friday = weeklyGoals.friday.intoDataStoreDailyGoal(),
        saturday = weeklyGoals.saturday.intoDataStoreDailyGoal(),
        sunday = weeklyGoals.sunday.intoDataStoreDailyGoal(),
    )

    fun toWeeklyGoals(): WeeklyGoals =
        WeeklyGoals(
            useSeparateGoals = useSeparateGoals,
            monday = monday.toDailyGoal(),
            tuesday = tuesday.toDailyGoal(),
            wednesday = wednesday.toDailyGoal(),
            thursday = thursday.toDailyGoal(),
            friday = friday.toDailyGoal(),
            saturday = saturday.toDailyGoal(),
            sunday = sunday.toDailyGoal(),
        )
}

@Serializable
private class DataStoreDailyGoal(
    val map: Map<NutritionFactsField, Double>,
    val isDistribution: Boolean,
) {
    fun toDailyGoal(): DailyGoal {
        val macronutrientGoal =
            if (isDistribution) {
                MacronutrientGoal.Distribution(
                    energyKcal =
                        map[NutritionFactsField.Energy]
                            ?: error("Energy must be set for distribution goal"),
                    proteinsPercentage =
                        map[NutritionFactsField.Proteins]
                            ?: error("Proteins must be set for distribution goal"),
                    fatsPercentage =
                        map[NutritionFactsField.Fats]
                            ?: error("Fats must be set for distribution goal"),
                    carbohydratesPercentage =
                        map[NutritionFactsField.Carbohydrates]
                            ?: error("Carbohydrates must be set for distribution goal"),
                )
            } else {
                MacronutrientGoal.Manual(
                    energyKcal =
                        map[NutritionFactsField.Energy]
                            ?: error("Energy must be set for manual goal"),
                    proteinsGrams =
                        map[NutritionFactsField.Proteins]
                            ?: error("Proteins must be set for manual goal"),
                    fatsGrams =
                        map[NutritionFactsField.Fats] ?: error("Fats must be set for manual goal"),
                    carbohydratesGrams =
                        map[NutritionFactsField.Carbohydrates]
                            ?: error("Carbohydrates must be set for manual goal"),
                )
            }

        return DailyGoal(macronutrientGoal = macronutrientGoal, map = map)
    }
}

private fun DailyGoal.intoDataStoreDailyGoal(): DataStoreDailyGoal {
    val macronutrientGoal = macronutrientGoal
    val energy = macronutrientGoal.energyKcal

    val proteins =
        when (macronutrientGoal) {
            is MacronutrientGoal.Manual -> macronutrientGoal.proteinsGrams
            is MacronutrientGoal.Distribution -> macronutrientGoal.proteinsPercentage
        }

    val fats =
        when (macronutrientGoal) {
            is MacronutrientGoal.Manual -> macronutrientGoal.fatsGrams
            is MacronutrientGoal.Distribution -> macronutrientGoal.fatsPercentage
        }

    val carbohydrates =
        when (macronutrientGoal) {
            is MacronutrientGoal.Manual -> macronutrientGoal.carbohydratesGrams
            is MacronutrientGoal.Distribution -> macronutrientGoal.carbohydratesPercentage
        }

    val newMap =
        map.toMutableMap().apply {
            this[NutritionFactsField.Energy] = energy
            this[NutritionFactsField.Proteins] = proteins
            this[NutritionFactsField.Fats] = fats
            this[NutritionFactsField.Carbohydrates] = carbohydrates
        }

    return DataStoreDailyGoal(map = newMap, isDistribution = isDistribution)
}
