package com.maksimowiczm.foodyou.search.infrastructure

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT $SIMPLE_NAME_SELECT
        FROM Search s
        ORDER BY simpleName
        """
    )
    fun getPagingSource(languageCode: String): PagingSource<Int, SearchEntity>

    @Query("SELECT COUNT(*) FROM Search s") fun observeCount(): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT $SIMPLE_NAME_SELECT
        FROM Search s
        JOIN SearchFts fts ON s.sqliteId = fts.rowid
        WHERE
            SearchFts MATCH :query || '*'
        ORDER BY simpleName
        """
    )
    fun getPagingSourceByQuery(query: String, languageCode: String): PagingSource<Int, SearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Search s
        JOIN SearchFts fts ON s.sqliteId = fts.rowid
        WHERE
            SearchFts MATCH :query || '*'
        """
    )
    fun observeCountByQuery(query: String): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT $SIMPLE_NAME_SELECT
        FROM Search s
        WHERE
            barcode LIKE '%' || :barcode || '%'
        ORDER BY simpleName
        """
    )
    fun getPagingSourceByBarcode(
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
    fun observeCountByBarcode(barcode: String): Flow<Int>

    @Insert suspend fun insert(searchEntityFts: SearchEntity)

    @Query("DELETE FROM Search WHERE productId = :productId")
    suspend fun deleteByProductId(productId: Uuid)
}

private const val SIMPLE_NAME_SELECT =
    """
s.sqliteId as sqliteId,
s.productId as productId,
s.name_en as name_en,
s.name_ca as name_ca,
s.name_cs as name_cs,
s.name_da as name_da,
s.name_de as name_de,
s.name_es as name_es,
s.name_fr as name_fr,
s.name_it as name_it,
s.name_id as name_id,
s.name_hu as name_hu,
s.name_nl as name_nl,
s.name_pl as name_pl,
s.name_sl as name_sl,
s.`name_pt-BR` as `name_pt-BR`,
s.name_tr as name_tr,
s.name_ru as name_ru,
s.name_uk as name_uk,
s.name_ar as name_ar,
s.`name_zh-CN` as `name_zh-CN`,
s.brand as brand,
s.barcode as barcode,
s.note as note,
s.imageDigest as imageDigest,
s.energy as energy,
s.proteins as proteins,
s.fats as fats,
s.saturatedFats as saturatedFats,
s.transFats as transFats,
s.monounsaturatedFats as monounsaturatedFats,
s.polyunsaturatedFats as polyunsaturatedFats,
s.omega3 as omega3,
s.omega6 as omega6,
s.carbohydrates as carbohydrates,
s.sugars as sugars,
s.addedSugars as addedSugars,
s.dietaryFiber as dietaryFiber,
s.solubleFiber as solubleFiber,
s.insolubleFiber as insolubleFiber,
s.salt as salt,
s.cholesterol as cholesterol,
s.caffeine as caffeine,
s.manganese as manganese,
s.magnesium as magnesium,
s.potassium as potassium,
s.calcium as calcium,
s.copper as copper,
s.zinc as zinc,
s.sodium as sodium,
s.iron as iron,
s.phosphorus as phosphorus,
s.selenium as selenium,
s.iodine as iodine,
s.chromium as chromium,
s.vitaminA as vitaminA,
s.vitaminB1 as vitaminB1,
s.vitaminB2 as vitaminB2,
s.vitaminB3 as vitaminB3,
s.vitaminB5 as vitaminB5,
s.vitaminB6 as vitaminB6,
s.vitaminB7 as vitaminB7,
s.vitaminB9 as vitaminB9,
s.vitaminB12 as vitaminB12,
s.vitaminC as vitaminC,
s.vitaminD as vitaminD,
s.vitaminE as vitaminE,
s.vitaminK as vitaminK,
s.package_type as package_type,
s.package_amount as package_amount,
s.package_unit as package_unit,
s.serving_type as serving_type,
s.serving_amount as serving_amount,
s.serving_unit as serving_unit,
s.isLiquid as isLiquid,
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
