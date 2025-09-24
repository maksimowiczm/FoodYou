package com.maksimowiczm.foodyou.food.search.infrastructure.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSourceType
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSourceTypeSQLConstants
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodSearchDao {

    @Query(
        """
        SELECT *
        FROM SearchEntry
        ORDER BY epochSeconds DESC
        LIMIT :limit
        """
    )
    fun observeRecentSearches(limit: Int): Flow<List<SearchEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchEntry(entry: SearchEntry)

    @Query(
        """
        WITH ProductsSearch AS (
            SELECT $PRODUCT_FOOD_SEARCH_SQL_SELECT
            FROM Product p
            WHERE
                (
                    (:query IS NULL OR p.name COLLATE NOCASE LIKE '%' || :query || '%') OR
                    (:query IS NULL OR p.brand COLLATE NOCASE LIKE '%' || :query || '%')
                ) AND
                (:source IS NULL OR p.sourceType = :source)
        ),
        RecipesSearch AS (
            SELECT $RECIPE_FOOD_SEARCH_SQL_SELECT
            FROM Recipe r
            WHERE
                -- All recipes are from the user
                :source = ${FoodSourceTypeSQLConstants.USER} AND
                (:query IS NULL OR r.name COLLATE NOCASE LIKE '%' || :query || '%') AND
                (:excludedRecipeId IS NULL OR r.id != :excludedRecipeId) AND
                (:excludedRecipeId IS NULL OR NOT EXISTS (
                    SELECT 1
                    FROM RecipeAllIngredientsView rai
                    WHERE rai.targetRecipeId = r.id 
                    AND rai.ingredientId = :excludedRecipeId
                ))
        )
        SELECT *, NULL AS measurementType, NULL AS measurementValue
        FROM ProductsSearch
        UNION ALL
        SELECT *, NULL AS measurementType, NULL AS measurementValue
        FROM RecipesSearch
        ORDER BY headline COLLATE NOCASE ASC
        """
    )
    fun observeFoodByQuery(
        query: String?,
        source: FoodSourceType?,
        excludedRecipeId: Long?,
    ): PagingSource<Int, FoodSearch>

    @Query(
        """
        WITH ProductsSearch AS (
            SELECT 1
            FROM Product p
            WHERE
                (
                    (:query IS NULL OR p.name COLLATE NOCASE LIKE '%' || :query || '%') OR
                    (:query IS NULL OR p.brand COLLATE NOCASE LIKE '%' || :query || '%')
                ) AND
                (:source IS NULL OR p.sourceType = :source)
        ),
        RecipesSearch AS (
            SELECT 1
            FROM Recipe r
            WHERE
                -- All recipes are from the user
                :source = ${FoodSourceTypeSQLConstants.USER} AND
                (:query IS NULL OR r.name COLLATE NOCASE LIKE '%' || :query || '%') AND
                (:excludedRecipeId IS NULL OR r.id != :excludedRecipeId) AND
                (:excludedRecipeId IS NULL OR NOT EXISTS (
                    SELECT 1
                    FROM RecipeAllIngredientsView rai
                    WHERE rai.targetRecipeId = r.id 
                    AND rai.ingredientId = :excludedRecipeId
                ))
        )
        SELECT (SELECT COUNT(*) FROM ProductsSearch) + (SELECT COUNT(*) FROM RecipesSearch) 
        """
    )
    fun observeFoodCountByQuery(
        query: String?,
        source: FoodSourceType?,
        excludedRecipeId: Long?,
    ): Flow<Int>

    @Query(
        """
        SELECT ${PRODUCT_FOOD_SEARCH_SQL_SELECT}, NULL AS measurementType, NULL AS measurementValue
        FROM Product p
        WHERE
            p.barcode = :barcode AND
            (:source IS NULL OR p.sourceType = :source)
        ORDER BY headline COLLATE NOCASE ASC
        """
    )
    fun observeFoodByBarcode(
        barcode: String,
        source: FoodSourceType?,
    ): PagingSource<Int, FoodSearch>

    @Query(
        """
        SELECT COUNT(*)
        FROM Product p
        WHERE
            p.barcode = :barcode AND
            (:source IS NULL OR p.sourceType = :source)
        """
    )
    fun observeFoodCountByBarcode(barcode: String, source: FoodSourceType?): Flow<Int>

    @Query(
        """
        WITH ProductsSearch AS (
            SELECT $PRODUCT_FOOD_SEARCH_SQL_SELECT, s.type AS measurementType, s.value AS measurementValue, s.epochSeconds AS epochSeconds
            FROM LatestMeasurementSuggestion s LEFT JOIN Product p ON s.productId = p.id
            WHERE
                s.productId IS NOT NULL AND
                s.epochSeconds >= :nowEpochSeconds - 2592000 AND
                (
                    (:query IS NULL OR p.name COLLATE NOCASE LIKE '%' || :query || '%') OR
                    (:query IS NULL OR p.brand COLLATE NOCASE LIKE '%' || :query || '%')
                )
        ),
        RecipesSearch AS (
            SELECT $RECIPE_FOOD_SEARCH_SQL_SELECT, s.type AS measurementType, s.value AS measurementValue, s.epochSeconds AS epochSeconds
            FROM LatestMeasurementSuggestion s LEFT JOIN Recipe r ON s.recipeId = r.id
            WHERE
                s.recipeId IS NOT NULL AND
                s.epochSeconds >= :nowEpochSeconds - 2592000 AND
                (:query IS NULL OR r.name COLLATE NOCASE LIKE '%' || :query || '%') AND
                (:excludedRecipeId IS NULL OR r.id != :excludedRecipeId) AND
                (:excludedRecipeId IS NULL OR NOT EXISTS (
                    SELECT 1
                    FROM RecipeAllIngredientsView rai
                    WHERE rai.targetRecipeId = r.id 
                    AND rai.ingredientId = :excludedRecipeId
                ))
        ),
        Merged AS (
            SELECT *
            FROM ProductsSearch
            UNION ALL
            SELECT *
            FROM RecipesSearch
        )
        SELECT $FOOD_SEARCH_SQL_SELECT
        FROM Merged
        ORDER BY epochSeconds DESC
        """
    )
    fun observeRecentFoodByQuery(
        query: String?,
        nowEpochSeconds: Long,
        excludedRecipeId: Long? = null,
    ): PagingSource<Int, FoodSearch>

    @Query(
        """
        WITH ProductsSearch AS (
            SELECT 1
            FROM LatestMeasurementSuggestion s LEFT JOIN Product p ON s.productId = p.id
            WHERE
                s.productId IS NOT NULL AND
                s.epochSeconds >= :nowEpochSeconds - 2592000 AND
                (
                    (:query IS NULL OR p.name COLLATE NOCASE LIKE '%' || :query || '%') OR
                    (:query IS NULL OR p.brand COLLATE NOCASE LIKE '%' || :query || '%')
                )
        ),
        RecipesSearch AS (
            SELECT 1
            FROM LatestMeasurementSuggestion s LEFT JOIN Recipe r ON s.recipeId = r.id
            WHERE
                s.recipeId IS NOT NULL AND
                s.epochSeconds >= :nowEpochSeconds - 2592000 AND
                (:query IS NULL OR r.name COLLATE NOCASE LIKE '%' || :query || '%') AND
                (:excludedRecipeId IS NULL OR r.id != :excludedRecipeId) AND
                (:excludedRecipeId IS NULL OR NOT EXISTS (
                    SELECT 1
                    FROM RecipeAllIngredientsView rai
                    WHERE rai.targetRecipeId = r.id 
                    AND rai.ingredientId = :excludedRecipeId
                ))
        )
        SELECT (SELECT COUNT(*) FROM ProductsSearch) + (SELECT COUNT(*) FROM RecipesSearch) 
        """
    )
    fun observeRecentFoodCountByQuery(
        query: String?,
        nowEpochSeconds: Long,
        excludedRecipeId: Long? = null,
    ): Flow<Int>

    @Query(
        """
        SELECT $PRODUCT_FOOD_SEARCH_SQL_SELECT, NULL AS measurementType, NULL AS measurementValue
        FROM LatestMeasurementSuggestion s LEFT JOIN Product p ON s.productId = p.id
        WHERE
            s.productId IS NOT NULL AND
            s.epochSeconds >= :nowEpochSeconds - 2592000 AND
            p.barcode = :barcode
        ORDER BY s.epochSeconds DESC
        """
    )
    fun observeRecentFoodByBarcode(
        barcode: String,
        nowEpochSeconds: Long,
    ): PagingSource<Int, FoodSearch>

    @Query(
        """
        SELECT COUNT(*)
        FROM LatestMeasurementSuggestion s LEFT JOIN Product p ON s.productId = p.id
        WHERE
            s.productId IS NOT NULL AND
            s.epochSeconds >= :nowEpochSeconds - 2592000 AND
            p.barcode = :barcode
        """
    )
    fun observeRecentFoodCountByBarcode(barcode: String, nowEpochSeconds: Long): Flow<Int>
}

// Don't do it twice
private const val PRODUCT_FOOD_SEARCH_SQL_SELECT =
    """
p.id AS productId, 
NULL AS recipeId,
CASE 
    WHEN p.brand IS NOT NULL THEN p.name || ' (' || p.brand || ')'
    ELSE p.name
END AS headline,
p.isLiquid,
p.energy,
p.proteins,
p.fats,
p.transFats,
p.saturatedFats,
p.monounsaturatedFats,
p.polyunsaturatedFats,
p.omega3,
p.omega6,
p.carbohydrates,
p.sugars,
p.addedSugars,
p.dietaryFiber,
p.solubleFiber,
p.insolubleFiber,
p.salt,
p.cholesterolMilli,
p.caffeineMilli,
p.vitaminAMicro,
p.vitaminB1Milli,
p.vitaminB2Milli,
p.vitaminB3Milli,
p.vitaminB5Milli,
p.vitaminB6Milli,
p.vitaminB7Micro,
p.vitaminB9Micro,
p.vitaminB12Micro,
p.vitaminCMilli,
p.vitaminDMicro,
p.vitaminEMilli,
p.vitaminKMicro,
p.manganeseMilli,
p.magnesiumMilli,
p.potassiumMilli,
p.calciumMilli,
p.copperMilli,
p.zincMilli,
p.sodiumMilli,
p.ironMilli,
p.phosphorusMilli,
p.seleniumMicro,
p.iodineMicro,
p.chromiumMicro,
p.packageWeight as totalWeight,
p.servingWeight as servingWeight
"""

private const val RECIPE_FOOD_SEARCH_SQL_SELECT =
    """
NULL AS productId,
r.id AS recipeId,
r.name AS headline,
r.isLiquid,
NULL AS energy,
NULL AS proteins,
NULL AS fats,
NULL AS transFats,
NULL AS saturatedFats,
NULL AS monounsaturatedFats,
NULL AS polyunsaturatedFats,
NULL AS omega3,
NULL AS omega6,
NULL AS carbohydrates,
NULL AS sugars,
NULL AS addedSugars,
NULL AS dietaryFiber,
NULL AS solubleFiber,
NULL AS insolubleFiber,
NULL AS salt,
NULL AS cholesterolMilli,
NULL AS caffeineMilli,
NULL AS vitaminAMicro,
NULL AS vitaminB1Milli,
NULL AS vitaminB2Milli,
NULL AS vitaminB3Milli,
NULL AS vitaminB5Milli,
NULL AS vitaminB6Milli,
NULL AS vitaminB7Micro,
NULL AS vitaminB9Micro,
NULL AS vitaminB12Micro,
NULL AS vitaminCMilli,
NULL AS vitaminDMicro,
NULL AS vitaminEMilli,
NULL AS vitaminKMicro,
NULL AS manganeseMilli,
NULL AS magnesiumMilli,
NULL AS potassiumMilli,
NULL AS calciumMilli,
NULL AS copperMilli,
NULL AS zincMilli,
NULL AS sodiumMilli,
NULL AS ironMilli,
NULL AS phosphorusMilli,
NULL AS seleniumMicro,
NULL AS iodineMicro,
NULL AS chromiumMicro,
NULL AS totalWeight,
NULL AS servingWeight
"""

private const val FOOD_SEARCH_SQL_SELECT =
    """
productId,
recipeId,
headline,
isLiquid,
energy,
proteins,
fats,
transFats,
saturatedFats,
monounsaturatedFats,
polyunsaturatedFats,
omega3,
omega6,
carbohydrates,
sugars,
addedSugars,
dietaryFiber,
solubleFiber,
insolubleFiber,
salt,
cholesterolMilli,
caffeineMilli,
vitaminAMicro,
vitaminB1Milli,
vitaminB2Milli,
vitaminB3Milli,
vitaminB5Milli,
vitaminB6Milli,
vitaminB7Micro,
vitaminB9Micro,
vitaminB12Micro,
vitaminCMilli,
vitaminDMicro,
vitaminEMilli,
vitaminKMicro,
manganeseMilli,
magnesiumMilli,
potassiumMilli,
calciumMilli,
copperMilli,
zincMilli,
sodiumMilli,
ironMilli,
phosphorusMilli,
seleniumMicro,
iodineMicro,
chromiumMicro,
totalWeight,
servingWeight,
measurementType,
measurementValue
"""
