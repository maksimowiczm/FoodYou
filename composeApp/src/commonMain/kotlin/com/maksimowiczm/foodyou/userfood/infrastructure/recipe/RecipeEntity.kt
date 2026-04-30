package com.maksimowiczm.foodyou.userfood.infrastructure.recipe

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

/**
 * @sqliteId Primary key for SQLite database, it is useful for FTS search.
 *
 * @uuid UUID of the recipe.
 */
@Entity(tableName = "Recipe", indices = [Index(value = ["uuid"], unique = true)])
internal data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val sqliteId: Long = 0,
    val uuid: Uuid,
    val name: String,
    val servings: Double,
    val imageDigest: String?,
    val note: String?,
)
