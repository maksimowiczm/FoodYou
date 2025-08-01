package com.maksimowiczm.foodyou.feature.fooddiary.domain

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyGoals(
    val monday: DailyGoal,
    val tuesday: DailyGoal,
    val wednesday: DailyGoal,
    val thursday: DailyGoal,
    val friday: DailyGoal,
    val saturday: DailyGoal,
    val sunday: DailyGoal
) {
    fun fillMissingFields() = WeeklyGoals(
        monday = monday.fillMissingFields(),
        tuesday = tuesday.fillMissingFields(),
        wednesday = wednesday.fillMissingFields(),
        thursday = thursday.fillMissingFields(),
        friday = friday.fillMissingFields(),
        saturday = saturday.fillMissingFields(),
        sunday = sunday.fillMissingFields()
    )

    val useSeparateGoals: Boolean
        get() = listOf(
            monday,
            tuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday
        ).distinct().size > 1

    companion object {
        val defaultGoals = WeeklyGoals(
            monday = DailyGoal.defaultGoals,
            tuesday = DailyGoal.defaultGoals,
            wednesday = DailyGoal.defaultGoals,
            thursday = DailyGoal.defaultGoals,
            friday = DailyGoal.defaultGoals,
            saturday = DailyGoal.defaultGoals,
            sunday = DailyGoal.defaultGoals
        )
    }
}
