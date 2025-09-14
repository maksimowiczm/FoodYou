package com.maksimowiczm.foodyou.app.infrastructure.opensource.food.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.FoodSourceType
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.Minerals
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.Nutrients
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.shared.Vitamins

@Entity(tableName = "Product")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String?,
    val barcode: String?,
    @Embedded val nutrients: Nutrients,
    @Embedded val vitamins: Vitamins,
    @Embedded val minerals: Minerals,
    val packageWeight: Double?,
    val servingWeight: Double?,
    val note: String?,
    val sourceType: FoodSourceType,
    val sourceUrl: String? = null,
    val isLiquid: Boolean,
)
