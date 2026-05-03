package com.maksimowiczm.foodyou.userfood.infrastructure.search

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SearchDao {
    @Query(
        """
        SELECT $PRODUCT_SELECT
        FROM Product p
        ORDER BY simpleName
        """
    )
    fun getPagingSource(languageCode: String): PagingSource<Int, UserFoodSearchEntity>

    @Query("""SELECT COUNT(*) FROM Product""") fun observeCount(): Flow<Int>

    @Query(
        """
        SELECT $PRODUCT_SELECT
        FROM Product p 
        JOIN ProductFts fts ON p.sqliteId = fts.rowid
        WHERE
            ProductFts MATCH :query || '*'
        ORDER BY simpleName
        """
    )
    fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
         SELECT COUNT(*) 
         FROM Product p 
         JOIN ProductFts fts ON p.sqliteId = fts.rowid
         WHERE
            ProductFts MATCH :query || '*'
        """
    )
    fun observeCountByQuery(query: String): Flow<Int>

    @Query(
        """
        SELECT
            $PRODUCT_SELECT
        FROM Product p
        WHERE
            barcode LIKE '%' || :barcode || '%'
        ORDER BY simpleName
        """
    )
    fun getPagingSourceByBarcode(
        barcode: String,
        languageCode: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Product
        WHERE
            barcode LIKE '%' || :barcode || '%'
        """
    )
    fun observeCountByBarcode(barcode: String): Flow<Int>
}

private const val PRODUCT_SELECT =
    """
p.sqliteId as p_sqliteId,
p.uuid as p_uuid,
p.name_en as p_name_en,
p.name_ca as p_name_ca,
p.name_cs as p_name_cs,
p.name_da as p_name_da,
p.name_de as p_name_de,
p.name_es as p_name_es,
p.name_fr as p_name_fr,
p.name_it as p_name_it,
p.name_id as p_name_id,
p.name_hu as p_name_hu,
p.name_nl as p_name_nl,
p.name_pl as p_name_pl,
p.name_sl as p_name_sl,
p.`name_pt-BR` as `p_name_pt-BR`,
p.name_tr as p_name_tr,
p.name_ru as p_name_ru,
p.name_uk as p_name_uk,
p.name_ar as p_name_ar,
p.`name_zh-CN` as `p_name_zh-CN`,
p.brand as p_brand,
p.barcode as p_barcode,
p.note as p_note,
p.imageDigest as p_imageDigest,
p.energy as p_energy,
p.proteins as p_proteins,
p.fats as p_fats,
p.saturatedFats as p_saturatedFats,
p.transFats as p_transFats,
p.monounsaturatedFats as p_monounsaturatedFats,
p.polyunsaturatedFats as p_polyunsaturatedFats,
p.omega3 as p_omega3,
p.omega6 as p_omega6,
p.carbohydrates as p_carbohydrates,
p.sugars as p_sugars,
p.addedSugars as p_addedSugars,
p.dietaryFiber as p_dietaryFiber,
p.solubleFiber as p_solubleFiber,
p.insolubleFiber as p_insolubleFiber,
p.salt as p_salt,
p.cholesterol as p_cholesterol,
p.caffeine as p_caffeine,
p.manganese as p_manganese,
p.magnesium as p_magnesium,
p.potassium as p_potassium,
p.calcium as p_calcium,
p.copper as p_copper,
p.zinc as p_zinc,
p.sodium as p_sodium,
p.iron as p_iron,
p.phosphorus as p_phosphorus,
p.selenium as p_selenium,
p.iodine as p_iodine,
p.chromium as p_chromium,
p.vitaminA as p_vitaminA,
p.vitaminB1 as p_vitaminB1,
p.vitaminB2 as p_vitaminB2,
p.vitaminB3 as p_vitaminB3,
p.vitaminB5 as p_vitaminB5,
p.vitaminB6 as p_vitaminB6,
p.vitaminB7 as p_vitaminB7,
p.vitaminB9 as p_vitaminB9,
p.vitaminB12 as p_vitaminB12,
p.vitaminC as p_vitaminC,
p.vitaminD as p_vitaminD,
p.vitaminE as p_vitaminE,
p.vitaminK as p_vitaminK,
p.package_type as p_package_type,
p.package_amount as p_package_amount,
p.package_unit as p_package_unit,
p.serving_type as p_serving_type,
p.serving_amount as p_serving_amount,
p.serving_unit as p_serving_unit,
NULL as p_isLiquid,
NULL as r_sqliteId,
NULL as r_uuid,
NULL as r_name,
NULL as r_servings,
NULL as r_imageDigest,
NULL as r_note,
CASE 
    WHEN p.brand IS NOT NULL THEN
        COALESCE(
            CASE :languageCode
                WHEN 'en-US' THEN p.name_en
                WHEN 'ca-ES' THEN p.name_ca
                WHEN 'cs-CZ' THEN p.name_cs
                WHEN 'da-DK' THEN p.name_da
                WHEN 'de-DE' THEN p.name_de
                WHEN 'es-ES' THEN p.name_es
                WHEN 'fr-FR' THEN p.name_fr
                WHEN 'it-IT' THEN p.name_it
                WHEN 'id-ID' THEN p.name_id
                WHEN 'hu-HU' THEN p.name_hu
                WHEN 'nl-NL' THEN p.name_nl
                WHEN 'pl-PL' THEN p.name_pl
                WHEN 'sl-SI' THEN p.name_sl
                WHEN 'pt-BR' THEN p.`name_pt-BR`
                WHEN 'tr-TR' THEN p.name_tr
                WHEN 'ru-RU' THEN p.name_ru
                WHEN 'uk-UA' THEN p.name_uk
                WHEN 'ar-SA' THEN p.name_ar
                WHEN 'zh-CN' THEN p.`name_zh-CN`
                ELSE p.name_en
            END,
            p.name_en,
            p.name_ca,
            p.name_cs,
            p.name_da,
            p.name_de,
            p.name_es,
            p.name_fr,
            p.name_it,
            p.name_id,
            p.name_hu,
            p.name_nl,
            p.name_pl,
            p.name_sl,
            p.`name_pt-BR`,
            p.name_tr,
            p.name_ru,
            p.name_uk,
            p.name_ar,
            p.`name_zh-CN`
        ) || ' (' || p.brand || ')'
    ELSE
        COALESCE(
            CASE :languageCode
                WHEN 'en-US' THEN p.name_en
                WHEN 'ca-ES' THEN p.name_ca
                WHEN 'cs-CZ' THEN p.name_cs
                WHEN 'da-DK' THEN p.name_da
                WHEN 'de-DE' THEN p.name_de
                WHEN 'es-ES' THEN p.name_es
                WHEN 'fr-FR' THEN p.name_fr
                WHEN 'it-IT' THEN p.name_it
                WHEN 'id-ID' THEN p.name_id
                WHEN 'hu-HU' THEN p.name_hu
                WHEN 'nl-NL' THEN p.name_nl
                WHEN 'pl-PL' THEN p.name_pl
                WHEN 'sl-SI' THEN p.name_sl
                WHEN 'pt-BR' THEN p.`name_pt-BR`
                WHEN 'tr-TR' THEN p.name_tr
                WHEN 'ru-RU' THEN p.name_ru
                WHEN 'uk-UA' THEN p.name_uk
                WHEN 'ar-SA' THEN p.name_ar
                WHEN 'zh-CN' THEN p.`name_zh-CN`
                ELSE p.name_en
            END,
            p.name_en,
            p.name_ca,
            p.name_cs,
            p.name_da,
            p.name_de,
            p.name_es,
            p.name_fr,
            p.name_it,
            p.name_id,
            p.name_hu,
            p.name_nl,
            p.name_pl,
            p.name_sl,
            p.`name_pt-BR`,
            p.name_tr,
            p.name_ru,
            p.name_uk,
            p.name_ar,
            p.`name_zh-CN`
        )
END as simpleName
"""
