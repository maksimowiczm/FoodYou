package com.maksimowiczm.foodyou.food.infrastructure.user.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserFood")
data class UserFoodEntity(
    @PrimaryKey val id: String,
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
)
