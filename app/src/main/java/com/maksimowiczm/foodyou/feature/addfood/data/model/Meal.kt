package com.maksimowiczm.foodyou.feature.addfood.data.model

import kotlinx.datetime.LocalTime

data class Meal(val id: Long, val name: String, val from: LocalTime, val to: LocalTime)
