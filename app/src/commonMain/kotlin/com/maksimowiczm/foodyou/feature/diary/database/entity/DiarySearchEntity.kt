package com.maksimowiczm.foodyou.feature.diary.database.entity

import androidx.room.Embedded

data class DiarySearchEntity(
    @Embedded(prefix = "p_")
    val product: ProductEntity?,

    @Embedded(prefix = "pm_")
    val weightMeasurement: WeightMeasurementEntity?,

    @Embedded(prefix = "r_")
    val recipeMeasurement: RecipeEntity?,

    @Embedded(prefix = "rm_")
    val recipe: RecipeMeasurementEntity?,

    val todaysMeasurement: Boolean
)
