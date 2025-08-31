package com.maksimowiczm.foodyou.feature.home.meals.card

import androidx.compose.runtime.Immutable
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import kotlinx.datetime.LocalTime

@Immutable
internal data class MealModel(
    val id: Long,
    val name: String,
    val from: LocalTime,
    val to: LocalTime,
    val isAllDay: Boolean,
    val foods: List<MealEntryModel>,
    val energy: Int,
    val proteins: Double,
    val carbohydrates: Double,
    val fats: Double,
)

@Immutable
internal data class MealEntryModel(
    val id: Long,
    val name: String,
    val energy: Int?,
    val proteins: Double?,
    val carbohydrates: Double?,
    val fats: Double?,
    val measurement: Measurement,
    val weight: Double?,
    val isLiquid: Boolean,
    val isRecipe: Boolean,
    val totalWeight: Double?,
    val servingWeight: Double?,
)
