package com.maksimowiczm.foodyou.app.business.shared.domain.settings

enum class HomeCard {
    Calendar,
    Goals,
    Meals;

    companion object {
        val defaultOrder: List<HomeCard> = listOf(Calendar, Goals, Meals)
    }
}
