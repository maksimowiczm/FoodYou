package com.maksimowiczm.foodyou.food.infrastructure.user.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFoodDao {
    @Upsert suspend fun upsert(userFoodEntity: UserFoodEntity)

    @Delete suspend fun delete(userFoodEntity: UserFoodEntity)

    @Query(
        """
        SELECT *
        FROM UserFood
        WHERE 
            accountId = :accountId AND
            id = :id
        LIMIT 1
        """
    )
    fun observe(id: String, accountId: String): Flow<UserFoodEntity?>

    @Query(
        """
        SELECT *
        FROM UserFood
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
    fun getPagingSource(languageCode: String, accountId: String): PagingSource<Int, UserFoodEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM UserFood
        WHERE accountId = :accountId
        """
    )
    fun observeCount(accountId: String): Flow<Int>

    @Query(
        """
        SELECT uf.*
        FROM UserFood uf JOIN UserFoodFts fts ON uf.id = fts.rowid
        WHERE 
            uf.accountId = :accountId AND
            UserFoodFts MATCH :query
        ORDER BY CASE :languageCode
            WHEN 'en-US' THEN uf.name_en
            WHEN 'ca-ES' THEN uf.name_ca
            WHEN 'da-DK' THEN uf.name_da
            WHEN 'de-DE' THEN uf.name_de
            WHEN 'es-ES' THEN uf.name_es
            WHEN 'fr-FR' THEN uf.name_fr
            WHEN 'it-IT' THEN uf.name_it
            WHEN 'hu-HU' THEN uf.name_hu
            WHEN 'nl-NL' THEN uf.name_nl
            WHEN 'pl-PL' THEN uf.name_pl
            WHEN 'pt-BR' THEN uf.`name_pt-BR`
            WHEN 'tr-TR' THEN uf.name_tr
            WHEN 'ru-RU' THEN uf.name_ru
            WHEN 'uk-UA' THEN uf.name_uk
            WHEN 'ar-SA' THEN uf.name_ar
            WHEN 'zh-CN' THEN uf.`name_zh-CN`
            ELSE uf.name_en
        END ASC, uf.name_en
        """
    )
    fun getPagingSourceByQuery(
        query: String,
        languageCode: String,
        accountId: String,
    ): PagingSource<Int, UserFoodEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM UserFood uf JOIN UserFoodFts fts ON uf.id = fts.rowid
        WHERE 
            uf.accountId = :accountId AND
            UserFoodFts MATCH :query
        """
    )
    fun observeCountByQuery(query: String, accountId: String): Flow<Int>

    @Query(
        """
        SELECT *
        FROM UserFood
        WHERE 
            accountId = :accountId AND
            barcode = :barcode
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
    ): PagingSource<Int, UserFoodEntity>

    @Query(
        """
        SELECT COUNT(*)
        FROM UserFood
        WHERE 
            accountId = :accountId AND
            barcode = :barcode
        """
    )
    fun observeCountByBarcode(barcode: String, accountId: String): Flow<Int>
}
