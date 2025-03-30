package com.maksimowiczm.foodyou.feature.diary.ui.mealscard.model

import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val isEmpty: Boolean
) {
    val isAllDay: Boolean
        get() = from == to
}
