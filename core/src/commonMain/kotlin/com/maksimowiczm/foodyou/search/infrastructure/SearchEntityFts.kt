package com.maksimowiczm.foodyou.search.infrastructure

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions

@Entity("SearchFts")
@Fts4(
    contentEntity = SearchEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"],
)
data class SearchEntityFts(
    @Embedded(prefix = "name_") val name: FoodNameEntity,
    val brand: String?,
    val barcode: String?,
    val note: String?,
)
