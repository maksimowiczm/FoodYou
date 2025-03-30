package com.maksimowiczm.foodyou.feature.diary.ui.addfoodproduct.model

import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

data class Food(
    val id: FoodId,
    val name: String,
    val nutrients: Nutrients,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val packageSuggestion: WeightMeasurement.Package?,
    val servingSuggestion: WeightMeasurement.Serving?,
    val weightSuggestions: WeightMeasurement.WeightUnit,
    val highlight: WeightMeasurementEnum?
)
