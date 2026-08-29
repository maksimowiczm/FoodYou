package com.maksimowiczm.foodyou.search.infrastructure

import androidx.room3.*
import com.maksimowiczm.foodyou.common.infrastructure.room.NutrientsEntity
import kotlin.uuid.Uuid

@Entity(
    tableName = "Search",
    indices =
        [Index(value = ["productId"], unique = true), Index(value = ["recipeId"], unique = true)],
)
data class SearchEntity(
    @PrimaryKey(autoGenerate = true) val sqliteId: Long = 0,
    val productId: Uuid?,
    val recipeId: Uuid?,
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val brand: String?,
    val barcode: String?,
    val note: String?,
    val imageDigest: String?,
    @Embedded val nutrients: NutrientsEntity,
    @Embedded(prefix = "package_") val packageSize: QuantityEntity?,
    @Embedded(prefix = "serving_") val servingSize: QuantityEntity?,
    val isLiquid: Boolean,
    val servings: Double?,
)
