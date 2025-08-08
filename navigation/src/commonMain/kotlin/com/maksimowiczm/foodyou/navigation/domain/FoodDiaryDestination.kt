package com.maksimowiczm.foodyou.navigation.domain

import kotlinx.serialization.Serializable

@Serializable internal data class FoodDiarySearchDestination(val epochDay: Long, val mealId: Long)
