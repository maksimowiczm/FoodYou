package com.maksimowiczm.foodyou.feature.diary.core.data.meal

import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int
)
