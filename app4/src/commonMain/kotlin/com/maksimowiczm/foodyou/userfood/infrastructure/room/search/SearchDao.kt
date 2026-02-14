package com.maksimowiczm.foodyou.userfood.infrastructure.room.search

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SearchDao {
    @Query(
        """
        SELECT
            $PRODUCT_SELECT,
            NULL as recipeId
        FROM Product p
        WHERE accountId = :accountId
        ORDER BY CASE :languageCode
            WHEN 'en-US' THEN name_en
            WHEN 'ca-ES' THEN name_ca
            WHEN 'da-DK' THEN name_da
            WHEN 'de-DE' THEN name_de
            WHEN 'es-ES' THEN name_es
            WHEN 'fr-FR' THEN name_fr
            WHEN 'it-IT' THEN name_it
            WHEN 'hu-HU' THEN name_hu
            WHEN 'nl-NL' THEN name_nl
            WHEN 'pl-PL' THEN name_pl
            WHEN 'pt-BR' THEN `name_pt-BR`
            WHEN 'tr-TR' THEN name_tr
            WHEN 'ru-RU' THEN name_ru
            WHEN 'uk-UA' THEN name_uk
            WHEN 'ar-SA' THEN name_ar
            WHEN 'zh-CN' THEN `name_zh-CN`
            ELSE name_en
        END ASC, name_en
        """
    )
    fun getPagingSource(
        languageCode: String,
        accountId: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Product
        WHERE accountId = :accountId
        """
    )
    fun observeCount(accountId: String): Flow<Int>

    @Query(
        """
        SELECT
            $PRODUCT_SELECT,
            NULL as recipeId
        FROM Product p JOIN ProductFts fts ON sqliteId = fts.rowid
        WHERE 
            accountId = :accountId AND
            ProductFts MATCH :query || '*'
        ORDER BY CASE :languageCode
            WHEN 'en-US' THEN fts.name_en
            WHEN 'ca-ES' THEN fts.name_ca
            WHEN 'da-DK' THEN fts.name_da
            WHEN 'de-DE' THEN fts.name_de
            WHEN 'es-ES' THEN fts.name_es
            WHEN 'fr-FR' THEN fts.name_fr
            WHEN 'it-IT' THEN fts.name_it
            WHEN 'hu-HU' THEN fts.name_hu
            WHEN 'nl-NL' THEN fts.name_nl
            WHEN 'pl-PL' THEN fts.name_pl
            WHEN 'pt-BR' THEN fts.`name_pt-BR`
            WHEN 'tr-TR' THEN fts.name_tr
            WHEN 'ru-RU' THEN fts.name_ru
            WHEN 'uk-UA' THEN fts.name_uk
            WHEN 'ar-SA' THEN fts.name_ar
            WHEN 'zh-CN' THEN fts.`name_zh-CN`
            ELSE fts.name_en
        END ASC, fts.name_en
        """
    )
    fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
        accountId: String,
    ): PagingSource<Int, UserFoodSearchEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM Product uf JOIN ProductFts fts ON uf.sqliteId = fts.rowid
        WHERE 
            uf.accountId = :accountId AND
            ProductFts MATCH :query || '*'
        """
    )
    fun observeCountByQuery(query: String, accountId: String): Flow<Int>

    @Query(
        """
        SELECT
            $PRODUCT_SELECT,
            NULL as recipeId
        FROM Product p
        WHERE 
            accountId = :accountId AND
            barcode LIKE '%' || :barcode || '%'
        ORDER BY CASE :languageCode
            WHEN 'en-US' THEN name_en
            WHEN 'ca-ES' THEN name_ca
            WHEN 'da-DK' THEN name_da
            WHEN 'de-DE' THEN name_de
            WHEN 'es-ES' THEN name_es
            WHEN 'fr-FR' THEN name_fr
            WHEN 'it-IT' THEN name_it
            WHEN 'hu-HU' THEN name_hu
            WHEN 'nl-NL' THEN name_nl
            WHEN 'pl-PL' THEN name_pl
            WHEN 'pt-BR' THEN `name_pt-BR`
            WHEN 'tr-TR' THEN name_tr
            WHEN 'ru-RU' THEN name_ru
            WHEN 'uk-UA' THEN name_uk
            WHEN 'ar-SA' THEN name_ar
            WHEN 'zh-CN' THEN `name_zh-CN`
            ELSE name_en
        END ASC, name_en
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
p.name_da as p_name_da,
p.name_de as p_name_de,
p.name_es as p_name_es,
p.name_fr as p_name_fr,
p.name_it as p_name_it,
p.name_hu as p_name_hu,
p.name_nl as p_name_nl,
p.name_pl as p_name_pl,
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
p.saturatedFats as p_saturatedfats,
p.transfats as p_transfats,
p.monounsaturatedFats as p_monounsaturatedfats,
p.polyunsaturatedFats as p_polyunsaturatedfats,
p.omega3 as p_omega3,
p.omega6 as p_omega6,
p.carbohydrates as p_carbohydrates,
p.sugars as p_sugars,
p.addedSugars as p_addedsugars,
p.dietaryFiber as p_dietaryfiber,
p.solubleFiber as p_solublefiber,
p.insolubleFiber as p_insolublefiber,
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
p.isLiquid as p_isLiquid
"""
