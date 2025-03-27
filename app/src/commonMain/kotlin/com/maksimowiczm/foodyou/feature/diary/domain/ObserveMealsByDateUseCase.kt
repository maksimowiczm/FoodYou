package com.maksimowiczm.foodyou.feature.diary.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun interface ObserveMealsByDateUseCase {
    data class Meal(
        val id: Long,
        val name: String,
        val from: LocalTime,
        val to: LocalTime,
        val rank: Int,
        val calories: Int,
        val proteins: Int,
        val carbohydrates: Int,
        val fats: Int,
        val isEmpty: Boolean
    ) {
        val isAllDay: Boolean
            get() = from == to
    }

    fun observeMealsByDate(date: LocalDate): Flow<List<Meal>>
    operator fun invoke(date: LocalDate): Flow<List<Meal>> = observeMealsByDate(date)
}
