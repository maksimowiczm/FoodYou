package com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.food

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.Minerals
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.Nutrients
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.shared.Vitamins

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
