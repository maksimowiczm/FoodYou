package com.maksimowiczm.foodyou.search.infrastructure

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Upsert
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SearchDao {
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT
        FROM Search s
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSource(languageCode: String): PagingSource<Int, SearchEntity>

    @Query("SELECT COUNT(*) FROM Search s") abstract fun observeCount(): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT
        FROM Search s
        JOIN SearchFts fts ON s.sqliteId = fts.rowid
        WHERE
            SearchFts MATCH :query || '*'
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
    ): PagingSource<Int, SearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Search s
        JOIN SearchFts fts ON s.sqliteId = fts.rowid
        WHERE
            SearchFts MATCH :query || '*'
        """
    )
    abstract fun observeCountByQuery(query: String): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*, $SIMPLE_NAME_SELECT
        FROM Search s
        WHERE
            barcode LIKE '%' || :barcode || '%'
        ORDER BY simpleName
        """
    )
    abstract fun getPagingSourceByBarcode(
        barcode: String,
        languageCode: String,
    ): PagingSource<Int, SearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Search s
        WHERE
            barcode LIKE '%' || :barcode || '%'
        """
    )
    abstract fun observeCountByBarcode(barcode: String): Flow<Int>

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

private const val SIMPLE_NAME_SELECT =
    """
CASE 
    WHEN s.brand IS NOT NULL THEN
        COALESCE(
            CASE :languageCode
                WHEN 'en-US' THEN s.name_en
                WHEN 'ca-ES' THEN s.name_ca
                WHEN 'cs-CZ' THEN s.name_cs
                WHEN 'da-DK' THEN s.name_da
                WHEN 'de-DE' THEN s.name_de
                WHEN 'es-ES' THEN s.name_es
                WHEN 'fr-FR' THEN s.name_fr
                WHEN 'it-IT' THEN s.name_it
                WHEN 'id-ID' THEN s.name_id
                WHEN 'hu-HU' THEN s.name_hu
                WHEN 'nl-NL' THEN s.name_nl
                WHEN 'pl-PL' THEN s.name_pl
                WHEN 'sl-SI' THEN s.name_sl
                WHEN 'pt-BR' THEN s.`name_pt-BR`
                WHEN 'tr-TR' THEN s.name_tr
                WHEN 'ru-RU' THEN s.name_ru
                WHEN 'uk-UA' THEN s.name_uk
                WHEN 'ar-SA' THEN s.name_ar
                WHEN 'zh-CN' THEN s.`name_zh-CN`
                ELSE s.name_en
            END,
            s.name_en,
            s.name_ca,
            s.name_cs,
            s.name_da,
            s.name_de,
            s.name_es,
            s.name_fr,
            s.name_it,
            s.name_id,
            s.name_hu,
            s.name_nl,
            s.name_pl,
            s.name_sl,
            s.`name_pt-BR`,
            s.name_tr,
            s.name_ru,
            s.name_uk,
            s.name_ar,
            s.`name_zh-CN`
        ) || ' (' || s.brand || ')'
    ELSE
        COALESCE(
            CASE :languageCode
                WHEN 'en-US' THEN s.name_en
                WHEN 'ca-ES' THEN s.name_ca
                WHEN 'cs-CZ' THEN s.name_cs
                WHEN 'da-DK' THEN s.name_da
                WHEN 'de-DE' THEN s.name_de
                WHEN 'es-ES' THEN s.name_es
                WHEN 'fr-FR' THEN s.name_fr
                WHEN 'it-IT' THEN s.name_it
                WHEN 'id-ID' THEN s.name_id
                WHEN 'hu-HU' THEN s.name_hu
                WHEN 'nl-NL' THEN s.name_nl
                WHEN 'pl-PL' THEN s.name_pl
                WHEN 'sl-SI' THEN s.name_sl
                WHEN 'pt-BR' THEN s.`name_pt-BR`
                WHEN 'tr-TR' THEN s.name_tr
                WHEN 'ru-RU' THEN s.name_ru
                WHEN 'uk-UA' THEN s.name_uk
                WHEN 'ar-SA' THEN s.name_ar
                WHEN 'zh-CN' THEN s.`name_zh-CN`
                ELSE s.name_en
            END,
            s.name_en,
            s.name_ca,
            s.name_cs,
            s.name_da,
            s.name_de,
            s.name_es,
            s.name_fr,
            s.name_it,
            s.name_id,
            s.name_hu,
            s.name_nl,
            s.name_pl,
            s.name_sl,
            s.`name_pt-BR`,
            s.name_tr,
            s.name_ru,
            s.name_uk,
            s.name_ar,
            s.`name_zh-CN`
        )
END as simpleName
"""
