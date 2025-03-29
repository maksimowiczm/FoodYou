package com.maksimowiczm.foodyou.feature.diary.data

import com.maksimowiczm.foodyou.feature.diary.data.model.FoodId
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightUnit

interface Food {
    val id: FoodId
    val name: String
    val brand: String?
    val nutrients: Nutrients
    val weightUnit: WeightUnit
    val packageWeight: Float?
    val servingWeight: Float?
}