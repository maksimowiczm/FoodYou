package com.maksimowiczm.foodyou.userfood.infrastructure.room.search

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
        WHERE accountId = :accountId

        UNION ALL

        SELECT $RECIPE_SELECT
        FROM Recipe r
        WHERE accountId = :accountId

        ORDER BY simpleName
        """
    )
    fun getPagingSource(
        languageCode: String,
        accountId: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
        SELECT 
            (SELECT COUNT(*) FROM Product WHERE accountId = :accountId) +
            (SELECT COUNT(*) FROM Recipe WHERE accountId = :accountId)
        """
    )
    fun observeCount(accountId: String): Flow<Int>

    @Query(
        """
        SELECT $PRODUCT_SELECT
        FROM Product p 
        JOIN ProductFts fts ON p.sqliteId = fts.rowid
        WHERE 
            p.accountId = :accountId AND
            ProductFts MATCH :query || '*'

        UNION ALL

        SELECT $RECIPE_SELECT
        FROM Recipe r 
        JOIN RecipeFts fts ON r.sqliteId = fts.rowid
        WHERE 
            r.accountId = :accountId AND
            RecipeFts MATCH :query || '*'

        ORDER BY simpleName
        """
    )
    fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
        accountId: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
        SELECT 
            (SELECT COUNT(*) 
             FROM Product p 
             JOIN ProductFts fts ON p.sqliteId = fts.rowid
             WHERE p.accountId = :accountId AND ProductFts MATCH :query || '*') +
            (SELECT COUNT(*) 
             FROM Recipe r 
             JOIN RecipeFts fts ON r.sqliteId = fts.rowid
             WHERE r.accountId = :accountId AND RecipeFts MATCH :query || '*')
        """
    )
    fun observeCountByQuery(query: String, accountId: String): Flow<Int>

    @Query(
        """
        SELECT
            $PRODUCT_SELECT
        FROM Product p
        WHERE 
            accountId = :accountId AND
            barcode LIKE '%' || :barcode || '%'
        ORDER BY simpleName
        """
    )
    fun getPagingSourceByBarcode(
        barcode: String,
        languageCode: String,
        accountId: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Product
        WHERE 
            accountId = :accountId AND
            barcode LIKE '%' || :barcode || '%'
        """
    )
    fun observeCountByBarcode(barcode: String, accountId: String): Flow<Int>
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
p.source as p_source,
p.photoPath as p_photoPath,
p.accountId as p_accountId,
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
NULL as r_imagePath,
NULL as r_note,
NULL as r_finalWeight,
NULL as r_accountId,
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
END as simpleName
"""

private const val RECIPE_SELECT =
    """
NULL as p_sqliteId,
NULL as p_uuid,
NULL as p_name_en,
NULL as p_name_ca,
NULL as p_name_cs,
NULL as p_name_da,
NULL as p_name_de,
NULL as p_name_es,
NULL as p_name_fr,
NULL as p_name_it,
NULL as p_name_id,
NULL as p_name_hu,
NULL as p_name_nl,
NULL as p_name_pl,
NULL as p_name_sl,
NULL as `p_name_pt-BR`,
NULL as p_name_tr,
NULL as p_name_ru,
NULL as p_name_uk,
NULL as p_name_ar,
NULL as `p_name_zh-CN`,
NULL as p_brand,
NULL as p_barcode,
NULL as p_note,
NULL as p_source,
NULL as p_photoPath,
NULL as p_accountId,
NULL as p_energy,
NULL as p_proteins,
NULL as p_fats,
NULL as p_saturatedFats,
NULL as p_transFats,
NULL as p_monounsaturatedFats,
NULL as p_polyunsaturatedFats,
NULL as p_omega3,
NULL as p_omega6,
NULL as p_carbohydrates,
NULL as p_sugars,
NULL as p_addedSugars,
NULL as p_dietaryFiber,
NULL as p_solubleFiber,
NULL as p_insolubleFiber,
NULL as p_salt,
NULL as p_cholesterol,
NULL as p_caffeine,
NULL as p_manganese,
NULL as p_magnesium,
NULL as p_potassium,
NULL as p_calcium,
NULL as p_copper,
NULL as p_zinc,
NULL as p_sodium,
NULL as p_iron,
NULL as p_phosphorus,
NULL as p_selenium,
NULL as p_iodine,
NULL as p_chromium,
NULL as p_vitaminA,
NULL as p_vitaminB1,
NULL as p_vitaminB2,
NULL as p_vitaminB3,
NULL as p_vitaminB5,
NULL as p_vitaminB6,
NULL as p_vitaminB7,
NULL as p_vitaminB9,
NULL as p_vitaminB12,
NULL as p_vitaminC,
NULL as p_vitaminD,
NULL as p_vitaminE,
NULL as p_vitaminK,
NULL as p_package_type,
NULL as p_package_amount,
NULL as p_package_unit,
NULL as p_serving_type,
NULL as p_serving_amount,
NULL as p_serving_unit,
NULL as p_isLiquid,
r.sqliteId as r_sqliteId,
r.uuid as r_uuid,
r.name as r_name,
r.servings as r_servings,
r.imagePath as r_imagePath,
r.note as r_note,
r.finalWeight as r_finalWeight,
r.accountId as r_accountId,
r.name as simpleName
"""
