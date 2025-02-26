package com.maksimowiczm.foodyou.data.model

import kotlinx.datetime.LocalTime

data class Meal(val id: Long, val name: String, val from: LocalTime, val to: LocalTime)
