package com.maksimowiczm.foodyou.feature.diary.database.entity

import androidx.room.Embedded

data class RecipeProductSearchEntity(
    @Embedded(prefix = "p_")
    val product: ProductEntity,
    @Embedded(prefix = "m_")
    val recipeProductEntryEntity: RecipeProductEntryEntity?,

    val flag: Boolean
)
