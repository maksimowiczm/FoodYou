package com.maksimowiczm.foodyou.userfood.infrastructure.room.search

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.FtsOptions
import com.maksimowiczm.foodyou.userfood.infrastructure.room.recipe.RecipeEntity

@Fts4(
    contentEntity = RecipeEntity::class,
    tokenizer = FtsOptions.TOKENIZER_UNICODE61,
    tokenizerArgs = ["remove_diacritics=2"],
)
@Entity(tableName = "RecipeFts")
internal data class RecipeFts(val name: String, val note: String?)
