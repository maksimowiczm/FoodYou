package com.maksimowiczm.foodyou.business.fooddiary.domain

data class WeeklyGoals(
    val useSeparateGoals: Boolean,
    val monday: DailyGoal,
    val tuesday: DailyGoal,
    val wednesday: DailyGoal,
    val thursday: DailyGoal,
    val friday: DailyGoal,
    val saturday: DailyGoal,
    val sunday: DailyGoal,
) {
    companion object {
        val defaultGoals =
            WeeklyGoals(
                useSeparateGoals = false,
                monday = DailyGoal.defaultGoals,
                tuesday = DailyGoal.defaultGoals,
                wednesday = DailyGoal.defaultGoals,
                thursday = DailyGoal.defaultGoals,
                friday = DailyGoal.defaultGoals,
                saturday = DailyGoal.defaultGoals,
                sunday = DailyGoal.defaultGoals,
            )
    }
}
