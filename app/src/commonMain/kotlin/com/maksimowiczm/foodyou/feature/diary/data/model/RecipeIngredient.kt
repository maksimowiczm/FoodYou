package com.maksimowiczm.foodyou.feature.diary.data.model

data class RecipeIngredient(
    val id: Long,
    override val product: Product,
    override val measurement: WeightMeasurement
) : ProductWithMeasurement
