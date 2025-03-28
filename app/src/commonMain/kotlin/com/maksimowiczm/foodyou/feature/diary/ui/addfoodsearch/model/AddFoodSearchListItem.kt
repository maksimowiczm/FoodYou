package com.maksimowiczm.foodyou.feature.diary.ui.addfoodsearch.model

import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementId
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement

data class AddFoodSearchListItem(
    val id: FoodId,
    val listId: String,
    val name: String,
    val brand: String?,
    val calories: Int,
    val proteins: Int,
    val carbohydrates: Int,
    val fats: Int,
    val weightMeasurement: WeightMeasurement,
    val measurementId: MeasurementId?
) {
    val isChecked: Boolean
        get() = measurementId != null
}
