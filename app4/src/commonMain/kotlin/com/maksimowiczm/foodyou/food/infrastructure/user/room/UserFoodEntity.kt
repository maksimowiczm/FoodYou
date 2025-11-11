package com.maksimowiczm.foodyou.food.infrastructure.user.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.maksimowiczm.foodyou.food.infrastructure.common.NutrientsEntity

@Entity(
    tableName = "UserFood",
    indices =
        [
            Index(value = ["uuid"]),
            Index(value = ["accountId"]),
            Index(value = ["uuid", "accountId"], unique = true),
        ],
)
/**
 * @sqliteId Primary key for SQLite database, it is useful for FTS search.
 * @uuid UUID of the food.
 */
data class UserFoodEntity(
    @PrimaryKey(autoGenerate = true) val sqliteId: Long,
    val uuid: String,
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val brand: String?,
    val barcode: String?,
    val note: String?,
    val source: String?,
    val photoPath: String?,
    val accountId: String,
    @Embedded val nutrients: NutrientsEntity,
    @Embedded(prefix = "package_") val packageSize: QuantityEntity?,
    @Embedded(prefix = "serving_") val servingSize: QuantityEntity?,
    val isLiquid: Boolean,
)
