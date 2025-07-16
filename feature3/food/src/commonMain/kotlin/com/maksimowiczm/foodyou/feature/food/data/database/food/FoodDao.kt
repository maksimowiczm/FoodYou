package com.maksimowiczm.foodyou.feature.food.data.database.food

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.maksimowiczm.foodyou.feature.food.domain.FoodSource
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query(
        """
        SELECT $FOOD_SEARCH_SQL_SELECT
        FROM Product p
        WHERE
            (
                (:query IS NULL OR p.name LIKE '%' || :query || '%') OR
                (:query IS NULL OR p.brand LIKE '%' || :query || '%')
            ) AND
            (:source IS NULL OR p.sourceType = :source)
        ORDER BY headline ASC
        """
    )
    fun observeFood(query: String?, source: FoodSource.Type?): PagingSource<Int, FoodSearch>

    @Query(
        """
        SELECT COUNT(*)
        FROM Product p
        WHERE
            (
                (:query IS NULL OR p.name LIKE '%' || :query || '%') OR
                (:query IS NULL OR p.brand LIKE '%' || :query || '%')
            ) AND
            (:source IS NULL OR p.sourceType = :source)
        """
    )
    fun observeFoodCountByQuery(query: String?, source: FoodSource.Type?): Flow<Int>

    @Query(
        """
        SELECT $FOOD_SEARCH_SQL_SELECT
        FROM Product p
        WHERE
            p.barcode = :barcode AND
            (:source IS NULL OR p.sourceType = :source)
        ORDER BY headline ASC
        """
    )
    fun observeFoodByBarcode(
        barcode: String,
        source: FoodSource.Type?
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
    fun observeFoodCountByBarcode(barcode: String, source: FoodSource.Type?): Flow<Int>
}

// Don't do it twice
private const val FOOD_SEARCH_SQL_SELECT = """
p.id AS productId, 
NULL AS recipeId,
CASE 
    WHEN p.brand IS NOT NULL THEN p.name || ' (' || p.brand || ')'
    ELSE p.name
END AS headline,
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
