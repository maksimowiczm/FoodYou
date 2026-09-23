package com.maksimowiczm.foodyou.search.infrastructure

import androidx.room3.*

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
