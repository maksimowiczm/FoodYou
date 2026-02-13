package com.maksimowiczm.foodyou.userfood.infrastructure.room.product

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Fts4(
    contentEntity = ProductEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"],
)
@Entity(tableName = "ProductFts")
internal data class ProductFts(
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val brand: String?,
    val note: String?,
)
