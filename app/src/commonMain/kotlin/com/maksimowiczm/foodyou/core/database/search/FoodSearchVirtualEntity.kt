package com.maksimowiczm.foodyou.core.database.search

import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.core.NutrientsEmbedded

data class FoodSearchVirtualEntity(
    val productId: Long,

    val name: String,
    val brand: String?,

    @Embedded
    val nutrients: NutrientsEmbedded,

    val packageWeight: Float?,
    val servingWeight: Float?
)
