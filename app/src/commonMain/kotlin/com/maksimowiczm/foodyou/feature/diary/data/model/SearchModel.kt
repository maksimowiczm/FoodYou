package com.maksimowiczm.foodyou.feature.diary.data.model

data class SearchModel(
    val foodId: FoodId,
    val name: String,
    val brand: String?,
    val calories: Float,
    val proteins: Float,
    val carbohydrates: Float,
    val fats: Float,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val measurement: WeightMeasurement
)
