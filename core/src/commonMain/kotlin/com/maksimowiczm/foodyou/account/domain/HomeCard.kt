package com.maksimowiczm.foodyou.account.domain

import kotlinx.serialization.Serializable

@Serializable
enum class HomeCard {
    Calendar,
    MealCards;

    companion object {
        val defaultOrder = listOf(Calendar, MealCards)
    }
}
