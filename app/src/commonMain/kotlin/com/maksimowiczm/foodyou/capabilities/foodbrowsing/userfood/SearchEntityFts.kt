package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import androidx.room3.*
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodNameEntity

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
