package com.maksimowiczm.foodyou.ui.settings.home

enum class HomeCard {
    Calendar,
    Meals,
    Calories
}

fun String?.toHomeCards() = this
    ?.split(",")
    ?.map { it.trim() }
    ?.map { HomeCard.valueOf(it) }
    ?: HomeCard.entries

fun List<HomeCard>.string() = joinToString(",") { it.name }
