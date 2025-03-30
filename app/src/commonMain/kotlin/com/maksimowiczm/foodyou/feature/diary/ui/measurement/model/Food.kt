package com.maksimowiczm.foodyou.feature.diary.ui.measurement.model

import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum

data class Food(
    val id: FoodId,
    val name: String,
    val nutrients: Nutrients,
    val packageWeight: Float?,
    val servingWeight: Float?,
    val suggestion: MeasurementSuggestion,
    val highlight: WeightMeasurementEnum?
)
