package com.maksimowiczm.foodyou.search.infrastructure

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.common.infrastructure.food.NutrientsEntity
import kotlin.uuid.Uuid

@Entity(tableName = "Search")
data class SearchEntity(
    @PrimaryKey(autoGenerate = true) val sqliteId: Long = 0,
    val productId: Uuid,
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val brand: String?,
    val barcode: String?,
    val note: String?,
    val imageDigest: String?,
    @Embedded val nutrients: NutrientsEntity,
    @Embedded(prefix = "package_") val packageSize: QuantityEntity?,
    @Embedded(prefix = "serving_") val servingSize: QuantityEntity?,
    val isLiquid: Boolean,
)
