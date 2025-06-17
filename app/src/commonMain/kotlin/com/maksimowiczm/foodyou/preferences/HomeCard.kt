package com.maksimowiczm.foodyou.preferences

enum class HomeCard {
    Calendar,
    Meals,
    Goals;

    companion object {
        val defaultOrder = listOf(
            Calendar,
            Goals,
            Meals
        )
    }
}
