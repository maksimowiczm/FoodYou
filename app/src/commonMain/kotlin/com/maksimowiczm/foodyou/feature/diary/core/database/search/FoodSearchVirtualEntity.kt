package com.maksimowiczm.foodyou.feature.diary.core.database.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.feature.diary.core.database.core.Nutrients

data class FoodSearchVirtualEntity(
    val productId: Long,

    val name: String,
    val brand: String?,

    @Embedded
    val nutrients: Nutrients,

    val packageWeight: Float?,
    val servingWeight: Float?
)
