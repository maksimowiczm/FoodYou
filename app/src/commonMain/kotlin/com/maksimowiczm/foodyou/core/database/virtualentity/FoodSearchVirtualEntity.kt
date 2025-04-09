package com.maksimowiczm.foodyou.core.database.virtualentity

import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.database.embedded.NutrientsEmbedded

data class FoodSearchVirtualEntity(
    val productId: Long,

    val name: String,
    val brand: String?,

    @Embedded
    val nutrients: NutrientsEmbedded,

    val packageWeight: Float?,
    val servingWeight: Float?
)
