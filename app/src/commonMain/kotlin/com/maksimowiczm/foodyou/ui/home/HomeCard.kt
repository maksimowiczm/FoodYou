package com.maksimowiczm.foodyou.ui.home

enum class HomeCard {
    Calendar,
    Meals,
    Calories
}

fun String?.toHomeCards() = runCatching {
    this
        ?.split(",")
        ?.map { it.trim() }
        ?.map { HomeCard.entries[it.toInt()] }
        ?: HomeCard.entries
}.getOrElse { HomeCard.entries }

fun List<HomeCard>.string() = joinToString(",") { it.ordinal.toString() }
