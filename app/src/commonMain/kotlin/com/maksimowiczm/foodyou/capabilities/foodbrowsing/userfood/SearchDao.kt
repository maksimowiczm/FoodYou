package com.maksimowiczm.foodyou.capabilities.foodbrowsing.userfood

import androidx.paging.PagingSource
import androidx.room3.*
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.SIMPLE_NAME_SELECT
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
abstract class SearchDao {
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT || COALESCE(' (' || s.brand || ')', '') as simpleName
        FROM Search s
        WHERE
            recipeId IS NULL OR recipeId NOT IN (:excludeRecipeIds)
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSource(
        languageCode: String,
        excludeRecipeIds: Set<Uuid>,
    ): PagingSource<Int, SearchEntity>

    @Query(
        """
        SELECT COUNT(*) 
        FROM Search
        WHERE
            recipeId IS NULL OR
            recipeId NOT IN (:excludeRecipeIds)
        """
    )
    abstract fun observeCount(excludeRecipeIds: Set<Uuid>): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT || COALESCE(' (' || s.brand || ')', '') as simpleName
        FROM Search s
        JOIN SearchFts fts ON s.sqliteId = fts.rowid
        WHERE
            SearchFts MATCH :query || '*' AND
            (recipeId IS NULL OR recipeId NOT IN (:excludeRecipeIds))
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
        excludeRecipeIds: Set<Uuid>,
    ): PagingSource<Int, SearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Search s
        JOIN SearchFts fts ON s.sqliteId = fts.rowid
        WHERE
            SearchFts MATCH :query || '*' AND
            (recipeId IS NULL OR recipeId NOT IN (:excludeRecipeIds))
        """
    )
    abstract fun observeCountByQuery(query: String, excludeRecipeIds: Set<Uuid>): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT || COALESCE(' (' || s.brand || ')', '') as simpleName
        FROM Search s
        WHERE
            barcode LIKE '%' || :barcode || '%' AND
            (recipeId IS NULL OR recipeId NOT IN (:excludeRecipeIds))
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSourceByBarcode(
        barcode: String,
        languageCode: String,
        excludeRecipeIds: Set<Uuid>,
    ): PagingSource<Int, SearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Search s
        WHERE
            barcode LIKE '%' || :barcode || '%' AND
            (recipeId IS NULL OR recipeId NOT IN (:excludeRecipeIds))
        """
    )
    abstract fun observeCountByBarcode(barcode: String, excludeRecipeIds: Set<Uuid>): Flow<Int>

    @Query("DELETE FROM Search WHERE productId = :productId")
    abstract suspend fun deleteByProductId(productId: Uuid)

    @Query("DELETE FROM Search WHERE recipeId = :recipeId")
    abstract suspend fun deleteByRecipeId(recipeId: Uuid)

    @Upsert protected abstract suspend fun upsertInternal(searchEntity: SearchEntity)

    @Query("SELECT sqliteId FROM Search WHERE productId = :productId")
    protected abstract suspend fun getIdByProductId(productId: Uuid): Long

    @Query("SELECT sqliteId FROM Search WHERE recipeId = :recipeId")
    protected abstract suspend fun getIdByRecipeId(recipeId: Uuid): Long

    @Transaction
    open suspend fun upsert(searchEntity: SearchEntity) {
        val id =
            when {
                searchEntity.productId != null -> getIdByProductId(searchEntity.productId)
                searchEntity.recipeId != null -> getIdByRecipeId(searchEntity.recipeId)
                else -> 0L
            }

        upsertInternal(searchEntity.copy(sqliteId = id))
    }
}
