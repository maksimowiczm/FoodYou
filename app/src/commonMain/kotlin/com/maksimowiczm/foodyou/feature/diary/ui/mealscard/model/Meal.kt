package com.maksimowiczm.foodyou.feature.diary.ui.mealscard.model

import kotlinx.datetime.LocalTime

data class Meal(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val fromString: String,
    val to: LocalTime,
    val toString: String,
    val rank: Int,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val isEmpty: Boolean
) {
    val isAllDay: Boolean
        get() = from == to

    companion object {
        fun empty(
            id: Long,
            name: String,
            from: LocalTime,
            fromString: String,
            to: LocalTime,
            toString: String,
            rank: Int
        ) = Meal(
            id = id,
            name = name,
            from = from,
            fromString = fromString,
            to = to,
            toString = toString,
            rank = rank,
            calories = 0,
            proteins = 0,
            carbohydrates = 0,
            fats = 0,
            isEmpty = true
        )
    }
}
