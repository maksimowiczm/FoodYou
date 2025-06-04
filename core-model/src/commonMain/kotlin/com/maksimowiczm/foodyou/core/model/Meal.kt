package com.maksimowiczm.foodyou.core.model

import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val rank: Int
) {
    val isAllDay: Boolean
        get() = from == to
}
