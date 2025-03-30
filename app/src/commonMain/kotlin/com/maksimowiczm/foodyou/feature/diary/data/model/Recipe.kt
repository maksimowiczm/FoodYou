package com.maksimowiczm.foodyou.feature.diary.data.model

import com.maksimowiczm.foodyou.feature.diary.data.Food

data class Recipe(
    override val id: FoodId.Recipe,
    override val name: String,
    override val brand: String?,
    override val nutrients: Nutrients,
    override val weightUnit: WeightUnit,
    override val packageWeight: Float?,
    override val servingWeight: Float?
) : Food
