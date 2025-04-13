package com.maksimowiczm.foodyou.core.database.measurement

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.database.measurement.MeasurementSQLConstants.SERVING
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MeasurementDao {
    @Insert
    abstract suspend fun addProductMeasurement(entity: ProductMeasurementEntity)

    @Insert
    abstract suspend fun addRecipeMeasurement(entity: RecipeMeasurementEntity)

    @Update
    abstract suspend fun updateProductMeasurement(entity: ProductMeasurementEntity)

    @Update
    abstract suspend fun updateRecipeMeasurement(entity: RecipeMeasurementEntity)

    @Query(
        """
        SELECT *
        FROM ProductMeasurementEntity
        WHERE id = :id
        AND isDeleted = 0
        """
    )
    abstract suspend fun getProductMeasurement(id: Long): ProductMeasurementEntity?

    @Query(
        """
        SELECT *
        FROM RecipeMeasurementEntity
        WHERE id = :id
        AND isDeleted = 0
        """
    )
    abstract suspend fun getRecipeMeasurement(id: Long): RecipeMeasurementEntity?

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    abstract suspend fun deleteProductMeasurement(id: Long)

    @Query(
        """
        UPDATE RecipeMeasurementEntity
        SET isDeleted = 1
        WHERE id = :id
        """
    )
    abstract suspend fun deleteRecipeMeasurement(id: Long)

    @Query(
        """
        UPDATE ProductMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    abstract suspend fun restoreProductMeasurement(id: Long)

    @Query(
        """
        UPDATE RecipeMeasurementEntity
        SET isDeleted = 0
        WHERE id = :id
        """
    )
    abstract suspend fun restoreRecipeMeasurement(id: Long)

    @Query(
        """
        SELECT
            p.id AS productId,
            p.name AS name,
            p.brand AS brand,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.sugars AS sugars,
            p.fats AS fats,
            p.saturatedFats AS saturatedFats,
            p.salt AS salt,
            p.sodium AS sodium,
            p.fiber AS fiber,
            m.id AS measurementId,
            m.measurement AS measurement,
            m.quantity AS quantity,
            m.createdAt AS createdAt
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE diaryEpochDay = :epochDay
        AND mealId = :mealId
        AND isDeleted = 0
        ORDER BY m.createdAt DESC
        """
    )
    abstract fun observeProductMeasurements(
        epochDay: Int,
        mealId: Long
    ): Flow<List<ProductMeasurementVirtualEntity>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT
            r.id AS r_id,
            r.name AS r_name,
            r.servings AS r_servings,
            m.id AS m_id,
            m.mealId AS m_mealId,
            m.epochDay AS m_epochDay,
            m.recipeId AS m_recipeId,
            m.measurement AS m_measurement,
            m.quantity AS m_quantity,
            m.createdAt AS m_createdAt,
            m.isDeleted AS m_isDeleted
        FROM RecipeEntity r
        LEFT JOIN RecipeMeasurementEntity m ON r.id = m.recipeId
        LEFT JOIN RecipeIngredientWithProductView i ON i.r_recipeId = r.id
        WHERE m.epochDay = :epochDay
        AND m.mealId = :mealId
        AND m.isDeleted = 0
        """
    )
    abstract fun observeRecipeMeasurements(
        epochDay: Int,
        mealId: Long
    ): Flow<List<RecipeMeasurementVirtualEntity>>

    @Transaction
    @Query(
        """
        SELECT DISTINCT
            r.id AS r_id,
            r.name AS r_name,
            r.servings AS r_servings,
            m.id AS m_id,
            m.mealId AS m_mealId,
            m.epochDay AS m_epochDay,
            m.recipeId AS m_recipeId,
            m.measurement AS m_measurement,
            m.quantity AS m_quantity,
            m.createdAt AS m_createdAt,
            m.isDeleted AS m_isDeleted
        FROM RecipeEntity r
        LEFT JOIN RecipeMeasurementEntity m ON r.id = m.recipeId
        LEFT JOIN RecipeIngredientWithProductView i ON i.r_recipeId = r.id
        WHERE m.id = :measurementId
        AND m.isDeleted = 0
        """
    )
    abstract fun observeRecipeMeasurement(
        measurementId: Long
    ): Flow<RecipeMeasurementVirtualEntity?>

    @Query(
        """
        SELECT
            p.id AS productId,
            p.name AS name,
            p.brand AS brand,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            p.calories AS calories,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.sugars AS sugars,
            p.fats AS fats,
            p.saturatedFats AS saturatedFats,
            p.salt AS salt,
            p.sodium AS sodium,
            p.fiber AS fiber,
            m.id AS measurementId,
            m.measurement AS measurement,
            m.quantity AS quantity,
            m.createdAt AS createdAt
        FROM ProductMeasurementEntity m
        LEFT JOIN ProductEntity p ON p.id = m.productId
        WHERE m.id = :measurementId
        AND m.isDeleted = 0
        """
    )
    abstract fun observeProductMeasurement(
        measurementId: Long
    ): Flow<ProductMeasurementVirtualEntity?>

    @Query(
        """
        SELECT 
            COALESCE(m.quantity,
                CASE 
                    WHEN p.servingWeight IS NOT NULL THEN 1
                    WHEN p.packageWeight IS NOT NULL THEN 1
                    ELSE 100
                END
            ) AS quantity,
            COALESCE(m.measurement,
                CASE 
                    WHEN p.servingWeight IS NOT NULL THEN $SERVING
                    WHEN p.packageWeight IS NOT NULL THEN $PACKAGE
                    ELSE $GRAM
                END
            ) AS measurement
        FROM ProductEntity p
        LEFT JOIN (
            SELECT * 
            FROM ProductMeasurementEntity 
            WHERE productId = :productId
            ORDER BY createdAt DESC
            LIMIT 1
        ) m ON m.productId = p.id
        WHERE p.id = :productId
        LIMIT 1
        """
    )
    abstract suspend fun getProductMeasurementSuggestion(productId: Long): SuggestionVirtualEntity

    @Query(
        """
        WITH LatestMeasurements AS (
            SELECT DISTINCT m1.quantity, m1.measurement
            FROM ProductMeasurementEntity m1
            JOIN (
                SELECT m2.measurement, MAX(m2.createdAt) AS maxCreatedAt
                FROM ProductMeasurementEntity m2
                WHERE m2.productId = :productId
                GROUP BY m2.measurement
                LIMIT 3
            ) latest ON m1.measurement = latest.measurement AND m1.createdAt = latest.maxCreatedAt
            WHERE m1.productId = :productId
            GROUP BY m1.measurement
        ),
        Defaults AS (
            SELECT
                p.id AS productId,
                $SERVING AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId AND p.servingWeight IS NOT NULL
            UNION ALL
            SELECT
                p.id AS productId,
                $PACKAGE AS measurement,
                1 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId AND p.packageWeight IS NOT NULL
            UNION ALL
            SELECT
                p.id AS productId,
                $GRAM AS measurement,
                100 AS quantity
            FROM ProductEntity p
            WHERE p.id = :productId
        )
        SELECT DISTINCT
            m.quantity, 
            m.measurement
        FROM LatestMeasurements m
        UNION ALL
        SELECT
            d.quantity, 
            d.measurement
        FROM Defaults d
        WHERE NOT EXISTS (
            SELECT 1 FROM LatestMeasurements lm WHERE lm.measurement = d.measurement
        )
        ORDER BY measurement DESC
        """
    )
    abstract suspend fun getProductMeasurementSuggestions(
        productId: Long
    ): List<SuggestionVirtualEntity>

    @Query(
        """
        WITH LatestMeasurements AS (
            SELECT DISTINCT m1.quantity, m1.measurement
            FROM RecipeMeasurementEntity m1
            JOIN (
                SELECT m2.measurement, MAX(m2.createdAt) AS maxCreatedAt
                FROM RecipeMeasurementEntity m2
                WHERE m2.recipeId = :recipeId
                GROUP BY m2.measurement
                LIMIT 3
            ) latest ON m1.measurement = latest.measurement AND m1.createdAt = latest.maxCreatedAt
            WHERE m1.recipeId = :recipeId
            GROUP BY m1.measurement
        ),
        Defaults AS (
            SELECT
                r.id AS recipeId,
                $SERVING AS measurement,
                1 AS quantity
            FROM RecipeEntity r
            WHERE r.id = :recipeId
            UNION ALL
            SELECT
                r.id AS recipeId,
                $PACKAGE AS measurement,
                1 AS quantity
            FROM RecipeEntity r
            WHERE r.id = :recipeId
            UNION ALL
            SELECT
                r.id AS recipeId,
                $GRAM AS measurement,
                100 AS quantity
            FROM RecipeEntity r
            WHERE r.id = :recipeId
        )
        SELECT DISTINCT
            m.quantity, 
            m.measurement
        FROM LatestMeasurements m
        UNION ALL
        SELECT
            d.quantity, 
            d.measurement
        FROM Defaults d
        WHERE NOT EXISTS (
            SELECT 1 FROM LatestMeasurements lm WHERE lm.measurement = d.measurement
        )
        ORDER BY measurement DESC
        """
    )
    abstract suspend fun getRecipeMeasurementSuggestions(
        recipeId: Long
    ): List<SuggestionVirtualEntity>
}
