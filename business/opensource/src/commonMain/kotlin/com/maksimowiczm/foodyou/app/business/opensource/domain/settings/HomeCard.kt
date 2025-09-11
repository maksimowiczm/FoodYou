package com.maksimowiczm.foodyou.app.business.opensource.domain.settings

enum class HomeCard {
    Calendar,
    Goals,
    Meals;

    companion object {
        val defaultOrder: List<HomeCard> = listOf(Calendar, Goals, Meals)
    }
}
