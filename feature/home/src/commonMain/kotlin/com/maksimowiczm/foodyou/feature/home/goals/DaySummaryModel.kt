package com.maksimowiczm.foodyou.feature.home.goals

import androidx.compose.runtime.Immutable

@Immutable
internal data class DaySummaryModel(
    val energy: Int,
    val energyGoal: Int,
    val proteins: Int,
    val proteinsGoal: Int,
    val carbohydrates: Int,
    val carbohydratesGoal: Int,
    val fats: Int,
    val fatsGoal: Int,
)
