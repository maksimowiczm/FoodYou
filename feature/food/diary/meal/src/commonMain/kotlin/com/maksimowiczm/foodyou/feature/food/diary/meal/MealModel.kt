package com.maksimowiczm.foodyou.feature.food.diary.meal

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalTime

@Immutable
data class MealModel(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val isAllDay: Boolean,
)
