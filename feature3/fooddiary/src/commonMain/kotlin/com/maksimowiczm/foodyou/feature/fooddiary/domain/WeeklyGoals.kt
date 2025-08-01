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

    companion object {
        val defaultGoals = WeeklyGoals(
            monday = DailyGoal.Companion.defaultGoals,
            tuesday = DailyGoal.Companion.defaultGoals,
            wednesday = DailyGoal.Companion.defaultGoals,
            thursday = DailyGoal.Companion.defaultGoals,
            friday = DailyGoal.Companion.defaultGoals,
            saturday = DailyGoal.Companion.defaultGoals,
            sunday = DailyGoal.Companion.defaultGoals
        )
    }
}
