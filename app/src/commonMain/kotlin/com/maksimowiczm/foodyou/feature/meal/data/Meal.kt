package com.maksimowiczm.foodyou.feature.meal.data

import kotlinx.datetime.LocalTime

internal data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int
)
