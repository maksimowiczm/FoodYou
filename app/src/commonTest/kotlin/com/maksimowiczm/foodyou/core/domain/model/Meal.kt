package com.maksimowiczm.foodyou.core.domain.model

import kotlinx.datetime.LocalTime

fun testMeal(
    id: Long = 1L,
    name: String = "Breakfast",
    from: LocalTime = LocalTime(8, 0),
    to: LocalTime = LocalTime(9, 0),
    rank: Int = 1
) = Meal(
    id = id,
    name = name,
    from = from,
    to = to,
    rank = rank
)
