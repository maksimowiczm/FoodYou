package com.maksimowiczm.foodyou.userfood.infrastructure.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(
    contentEntity = UserFoodEntity::class,
    tokenizer = androidx.room.FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"],
)
@Entity(tableName = "UserFoodFts")
internal data class UserFoodFts(
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val brand: String?,
    val note: String?,
)
