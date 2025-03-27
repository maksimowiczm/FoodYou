package com.maksimowiczm.foodyou.feature.diary.domain

import com.maksimowiczm.foodyou.feature.diary.domain.model.WeightMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun interface ObserveMealByDateUseCase {
    data class Product(
        val name: String,
        val brand: String?,
        val calories: Int,
        val proteins: Int,
        val carbohydrates: Int,
        val fats: Int,
        val weightMeasurement: WeightMeasurement
    )

    data class Meal(
        val id: Long,
        val name: String,
        val from: LocalTime,
        val to: LocalTime,
        val isAllDay: Boolean,
        val products: List<Product>
    )

    fun observeMealByDateUseCase(
        date: LocalDate,
        mealId: Long
    ): Flow<Meal>
}