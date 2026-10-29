package com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.Fts4
import androidx.room3.FtsOptions
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodNameEntity

@Entity("SwissFoodCompositionFts")
@Fts4(
    contentEntity = SwissFoodCompositionEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"],
)
data class SwissFoodCompositionEntityFts(@Embedded(prefix = "name_") val name: FoodNameEntity)
