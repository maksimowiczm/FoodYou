package com.maksimowiczm.foodyou.feature.diary.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime

fun interface ObserveMealsUseCase {
    data class Meal(
        val id: Long,
        val name: String,
        val from: LocalTime,
        val to: LocalTime,
        val rank: Int
    ) {
        val isAllDay: Boolean
            get() = from == to
    }

    fun observeMeals(): Flow<List<Meal>>
    operator fun invoke(): Flow<List<Meal>> = observeMeals()
}
