package com.maksimowiczm.foodyou.recipe.infrastructure.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Recipe",
    indices =
        [
            Index(value = ["uuid"]),
            Index(value = ["accountId"]),
            Index(value = ["uuid", "accountId"], unique = true),
        ],
)
/**
 * @sqliteId Primary key for SQLite database, it is useful for FTS search.
 *
 * @uuid UUID of the recipe.
 */
internal data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val sqliteId: Long = 0,
    val uuid: String,
    val name: String,
    val servings: Double,
    val imagePath: String?,
    val note: String?,
    val finalWeight: Double?,
    val accountId: String,
)
