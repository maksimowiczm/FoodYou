package com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.business.fooddiary.domain.DailyGoal
import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFactsField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class DataStoreGoalsDataSource(private val dataStore: DataStore<Preferences>) {
    fun observeWeeklyGoals(): Flow<WeeklyGoals> =
        dataStore.data.map { preferences ->
            preferences[GoalsDataStoreKeys.weeklyGoals]?.let { serialized ->
                Json.decodeFromString<DataStoreWeeklyGoals>(serialized).toWeeklyGoals()
            } ?: WeeklyGoals.defaultGoals
        }

    suspend fun updateWeeklyGoals(weeklyGoals: WeeklyGoals) {
        dataStore.updateData {
            it.toMutablePreferences().apply {
                val serialized = Json.encodeToString(DataStoreWeeklyGoals(weeklyGoals))
                set(GoalsDataStoreKeys.weeklyGoals, serialized)
            }
        }
    }
}

private object GoalsDataStoreKeys {
    val weeklyGoals = stringPreferencesKey("fooddiary:weekly_goals")
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
        monday = DataStoreDailyGoal(weeklyGoals.monday),
        tuesday = DataStoreDailyGoal(weeklyGoals.tuesday),
        wednesday = DataStoreDailyGoal(weeklyGoals.wednesday),
        thursday = DataStoreDailyGoal(weeklyGoals.thursday),
        friday = DataStoreDailyGoal(weeklyGoals.friday),
        saturday = DataStoreDailyGoal(weeklyGoals.saturday),
        sunday = DataStoreDailyGoal(weeklyGoals.sunday),
    )

    fun toWeeklyGoals(): WeeklyGoals =
        WeeklyGoals(
            useSeparateGoals = useSeparateGoals,
            monday = DailyGoal(monday.map, monday.isDistribution),
            tuesday = DailyGoal(tuesday.map, tuesday.isDistribution),
            wednesday = DailyGoal(wednesday.map, wednesday.isDistribution),
            thursday = DailyGoal(thursday.map, thursday.isDistribution),
            friday = DailyGoal(friday.map, friday.isDistribution),
            saturday = DailyGoal(saturday.map, saturday.isDistribution),
            sunday = DailyGoal(sunday.map, sunday.isDistribution),
        )
}

@Serializable
private class DataStoreDailyGoal(
    val map: Map<NutritionFactsField, Double>,
    val isDistribution: Boolean,
) {
    constructor(
        dailyGoal: DailyGoal
    ) : this(map = dailyGoal.map, isDistribution = dailyGoal.isDistribution)
}
