package com.maksimowiczm.foodyou.foodyou3.infrastructure.food

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3FoodSourceType
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3FoodSourceTypeConverter
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3FoodSourceTypeSQLConstants
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3MeasurementTypeConverter
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3Minerals
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3Nutrients
import com.maksimowiczm.foodyou.foodyou3.infrastructure.FoodYou3Vitamins

internal class SqliteFoodYou3FoodSearchDao(private val connection: SQLiteConnection) {
    fun getFood(
        source: FoodYou3FoodSourceType?,
        limit: Int,
        offset: Int,
    ): List<FoodYou3FoodSearch> {
        val sql =
            """
            WITH ProductsSearch AS (
                SELECT $PRODUCT_FOOD_SEARCH_SQL_SELECT
                FROM Product p
                WHERE (?1 IS NULL OR p.sourceType = ?1)
            ),
            RecipesSearch AS (
                SELECT $RECIPE_FOOD_SEARCH_SQL_SELECT
                FROM Recipe r
                WHERE
                    ?1 = ${FoodYou3FoodSourceTypeSQLConstants.USER}
            )
            SELECT *, NULL AS measurementType, NULL AS measurementValue
            FROM ProductsSearch
            UNION ALL
            SELECT *, NULL AS measurementType, NULL AS measurementValue
            FROM RecipesSearch
            ORDER BY headline COLLATE NOCASE ASC
            LIMIT ?2 OFFSET ?3
        """
        return queryList(
            sql,
            bind = { stmt ->
                if (source == null) stmt.bindNull(1)
                else
                    stmt.bindLong(
                        1,
                        FoodYou3FoodSourceTypeConverter.fromFoodSourceType(source).toLong(),
                    )
                stmt.bindLong(2, limit.toLong())
                stmt.bindLong(3, offset.toLong())
            },
            mapper = ::mapFoodSearchRow,
        )
    }

    fun getFoodCount(source: FoodYou3FoodSourceType?): Int {
        val sql =
            """
            WITH ProductsSearch AS (
                SELECT 1
                FROM Product p
                WHERE (?1 IS NULL OR p.sourceType = ?1)
            ),
            RecipesSearch AS (
                SELECT 1
                FROM Recipe r
                WHERE
                    ?1 = ${FoodYou3FoodSourceTypeSQLConstants.USER}
            )
            SELECT (SELECT COUNT(*) FROM ProductsSearch) + (SELECT COUNT(*) FROM RecipesSearch)
        """
        return queryScalar(sql) { stmt ->
            if (source == null) stmt.bindNull(1)
            else
                stmt.bindLong(
                    1,
                    FoodYou3FoodSourceTypeConverter.fromFoodSourceType(source).toLong(),
                )
        }
    }

    fun getFoodByQuery(
        query: String,
        source: FoodYou3FoodSourceType?,
        limit: Int,
        offset: Int,
    ): List<FoodYou3FoodSearch> {
        val sql =
            """
            WITH ProductsSearch AS (
                SELECT $PRODUCT_FOOD_SEARCH_SQL_SELECT
                FROM Product p JOIN ProductFts fts ON p.id = fts.rowid
                WHERE
                    (ProductFts MATCH ?1 || '*') AND (?2 IS NULL OR p.sourceType = ?2)
            ),
            RecipesSearch AS (
                SELECT $RECIPE_FOOD_SEARCH_SQL_SELECT
                FROM Recipe r JOIN RecipeFts fts ON r.id = fts.rowid
                WHERE
                    ?2 = ${FoodYou3FoodSourceTypeSQLConstants.USER} AND
                    (RecipeFts MATCH ?1 || '*')
            )
            SELECT *, NULL AS measurementType, NULL AS measurementValue
            FROM ProductsSearch
            UNION ALL
            SELECT *, NULL AS measurementType, NULL AS measurementValue
            FROM RecipesSearch
            ORDER BY headline COLLATE NOCASE ASC
            LIMIT ?3 OFFSET ?4
        """
        return queryList(
            sql,
            bind = { stmt ->
                stmt.bindText(1, query)
                if (source == null) stmt.bindNull(2)
                else
                    stmt.bindLong(
                        2,
                        FoodYou3FoodSourceTypeConverter.fromFoodSourceType(source).toLong(),
                    )
                stmt.bindLong(3, limit.toLong())
                stmt.bindLong(4, offset.toLong())
            },
            mapper = ::mapFoodSearchRow,
        )
    }

    fun getFoodCountByQuery(
        query: String,
        source: FoodYou3FoodSourceType?,
    ): Int {
        val sql =
            """
            WITH ProductsSearch AS (
                SELECT 1
                FROM Product p JOIN ProductFts fts ON p.id = fts.rowid
                WHERE
                    (ProductFts MATCH ?1 || '*') AND (?2 IS NULL OR p.sourceType = ?2)
            ),
            RecipesSearch AS (
                SELECT 1
                FROM Recipe r JOIN RecipeFts fts ON r.id = fts.rowid
                WHERE
                    ?2 = ${FoodYou3FoodSourceTypeSQLConstants.USER} AND
                    (RecipeFts MATCH ?1 || '*')
            )
            SELECT (SELECT COUNT(*) FROM ProductsSearch) + (SELECT COUNT(*) FROM RecipesSearch)
        """
        return queryScalar(sql) { stmt ->
            stmt.bindText(1, query)
            if (source == null) stmt.bindNull(2)
            else
                stmt.bindLong(
                    2,
                    FoodYou3FoodSourceTypeConverter.fromFoodSourceType(source).toLong(),
                )
        }
    }

    fun getFoodByBarcode(
        barcode: String,
        source: FoodYou3FoodSourceType?,
        limit: Int,
        offset: Int,
    ): List<FoodYou3FoodSearch> {
        val sql =
            """
            SELECT ${PRODUCT_FOOD_SEARCH_SQL_SELECT}, NULL AS measurementType, NULL AS measurementValue
            FROM Product p
            WHERE
                p.barcode LIKE '%' || ?1 || '%' AND
                (?2 IS NULL OR p.sourceType = ?2)
            ORDER BY headline COLLATE NOCASE ASC
            LIMIT ?3 OFFSET ?4
        """
        return queryList(
            sql,
            bind = { stmt ->
                stmt.bindText(1, barcode)
                if (source == null) stmt.bindNull(2)
                else
                    stmt.bindLong(
                        2,
                        FoodYou3FoodSourceTypeConverter.fromFoodSourceType(source).toLong(),
                    )
                stmt.bindLong(3, limit.toLong())
                stmt.bindLong(4, offset.toLong())
            },
            mapper = ::mapFoodSearchRow,
        )
    }

    fun getFoodCountByBarcode(
        barcode: String,
        source: FoodYou3FoodSourceType?,
    ): Int {
        val sql =
            """
            SELECT COUNT(*)
            FROM Product p
            WHERE
                p.barcode LIKE '%' || ?1 || '%' AND
                (?2 IS NULL OR p.sourceType = ?2)
        """
        return queryScalar(sql) { stmt ->
            stmt.bindText(1, barcode)
            if (source == null) stmt.bindNull(2)
            else
                stmt.bindLong(
                    2,
                    FoodYou3FoodSourceTypeConverter.fromFoodSourceType(source).toLong(),
                )
        }
    }

    private inline fun <T> queryList(
        sql: String,
        bind: (SQLiteStatement) -> Unit = {},
        mapper: (SQLiteStatement, Map<String, Int>) -> T,
    ): List<T> {
        val list = mutableListOf<T>()
        val stmt = connection.prepare(sql)
        try {
            bind(stmt)
            val columnMap = stmt.columnMap()
            while (stmt.step()) {
                list.add(mapper(stmt, columnMap))
            }
        } finally {
            stmt.close()
        }
        return list
    }

    private fun queryScalar(sql: String, bind: (SQLiteStatement) -> Unit = {}): Int {
        val stmt = connection.prepare(sql)
        try {
            bind(stmt)
            if (stmt.step()) {
                return stmt.getLong(0).toInt()
            }
        } finally {
            stmt.close()
        }
        return 0
    }
}

private fun SQLiteStatement.columnMap(): Map<String, Int> {
    val count = getColumnCount()
    val map = HashMap<String, Int>(count)
    for (i in 0 until count) {
        map[getColumnName(i)] = i
    }
    return map
}

private fun Map<String, Int>.getIndex(name: String): Int = getOrElse(name) { -1 }

private fun SQLiteStatement.getNullableDouble(index: Int): Double? =
    if (index == -1 || isNull(index)) null else getDouble(index)

private fun SQLiteStatement.getNullableLong(index: Int): Long? =
    if (index == -1 || isNull(index)) null else getLong(index)

private fun mapFoodSearchRow(stmt: SQLiteStatement, cols: Map<String, Int>): FoodYou3FoodSearch {
    val productId = stmt.getNullableLong(cols.getIndex("productId"))
    val recipeId = stmt.getNullableLong(cols.getIndex("recipeId"))
    val headline = stmt.getText(cols.getIndex("headline"))
    val isLiquid = stmt.getBoolean(cols.getIndex("isLiquid"))

    val energy = stmt.getNullableDouble(cols.getIndex("energy"))
    val proteins = stmt.getNullableDouble(cols.getIndex("proteins"))
    val fats = stmt.getNullableDouble(cols.getIndex("fats"))
    val saturatedFats = stmt.getNullableDouble(cols.getIndex("saturatedFats"))
    val transFats = stmt.getNullableDouble(cols.getIndex("transFats"))
    val monounsaturatedFats = stmt.getNullableDouble(cols.getIndex("monounsaturatedFats"))
    val polyunsaturatedFats = stmt.getNullableDouble(cols.getIndex("polyunsaturatedFats"))
    val omega3 = stmt.getNullableDouble(cols.getIndex("omega3"))
    val omega6 = stmt.getNullableDouble(cols.getIndex("omega6"))
    val carbohydrates = stmt.getNullableDouble(cols.getIndex("carbohydrates"))
    val sugars = stmt.getNullableDouble(cols.getIndex("sugars"))
    val addedSugars = stmt.getNullableDouble(cols.getIndex("addedSugars"))
    val dietaryFiber = stmt.getNullableDouble(cols.getIndex("dietaryFiber"))
    val solubleFiber = stmt.getNullableDouble(cols.getIndex("solubleFiber"))
    val insolubleFiber = stmt.getNullableDouble(cols.getIndex("insolubleFiber"))
    val salt = stmt.getNullableDouble(cols.getIndex("salt"))
    val cholesterolMilli = stmt.getNullableDouble(cols.getIndex("cholesterolMilli"))
    val caffeineMilli = stmt.getNullableDouble(cols.getIndex("caffeineMilli"))

    val vitaminAMicro = stmt.getNullableDouble(cols.getIndex("vitaminAMicro"))
    val vitaminB1Milli = stmt.getNullableDouble(cols.getIndex("vitaminB1Milli"))
    val vitaminB2Milli = stmt.getNullableDouble(cols.getIndex("vitaminB2Milli"))
    val vitaminB3Milli = stmt.getNullableDouble(cols.getIndex("vitaminB3Milli"))
    val vitaminB5Milli = stmt.getNullableDouble(cols.getIndex("vitaminB5Milli"))
    val vitaminB6Milli = stmt.getNullableDouble(cols.getIndex("vitaminB6Milli"))
    val vitaminB7Micro = stmt.getNullableDouble(cols.getIndex("vitaminB7Micro"))
    val vitaminB9Micro = stmt.getNullableDouble(cols.getIndex("vitaminB9Micro"))
    val vitaminB12Micro = stmt.getNullableDouble(cols.getIndex("vitaminB12Micro"))
    val vitaminCMilli = stmt.getNullableDouble(cols.getIndex("vitaminCMilli"))
    val vitaminDMicro = stmt.getNullableDouble(cols.getIndex("vitaminDMicro"))
    val vitaminEMilli = stmt.getNullableDouble(cols.getIndex("vitaminEMilli"))
    val vitaminKMicro = stmt.getNullableDouble(cols.getIndex("vitaminKMicro"))

    val manganeseMilli = stmt.getNullableDouble(cols.getIndex("manganeseMilli"))
    val magnesiumMilli = stmt.getNullableDouble(cols.getIndex("magnesiumMilli"))
    val potassiumMilli = stmt.getNullableDouble(cols.getIndex("potassiumMilli"))
    val calciumMilli = stmt.getNullableDouble(cols.getIndex("calciumMilli"))
    val copperMilli = stmt.getNullableDouble(cols.getIndex("copperMilli"))
    val zincMilli = stmt.getNullableDouble(cols.getIndex("zincMilli"))
    val sodiumMilli = stmt.getNullableDouble(cols.getIndex("sodiumMilli"))
    val ironMilli = stmt.getNullableDouble(cols.getIndex("ironMilli"))
    val phosphorusMilli = stmt.getNullableDouble(cols.getIndex("phosphorusMilli"))
    val seleniumMicro = stmt.getNullableDouble(cols.getIndex("seleniumMicro"))
    val iodineMicro = stmt.getNullableDouble(cols.getIndex("iodineMicro"))
    val chromiumMicro = stmt.getNullableDouble(cols.getIndex("chromiumMicro"))

    val totalWeight = stmt.getNullableDouble(cols.getIndex("totalWeight"))
    val servingWeight = stmt.getNullableDouble(cols.getIndex("servingWeight"))

    val measurementTypeInt = stmt.getNullableLong(cols.getIndex("measurementType"))?.toInt()
    val measurementType = measurementTypeInt?.let {
        FoodYou3MeasurementTypeConverter.toWeightMeasurementType(it)
    }
    val measurementValue = stmt.getNullableDouble(cols.getIndex("measurementValue"))

    val nutrients =
        FoodYou3Nutrients(
            energy = energy,
            proteins = proteins,
            fats = fats,
            saturatedFats = saturatedFats,
            transFats = transFats,
            monounsaturatedFats = monounsaturatedFats,
            polyunsaturatedFats = polyunsaturatedFats,
            omega3 = omega3,
            omega6 = omega6,
            carbohydrates = carbohydrates,
            sugars = sugars,
            addedSugars = addedSugars,
            dietaryFiber = dietaryFiber,
            solubleFiber = solubleFiber,
            insolubleFiber = insolubleFiber,
            salt = salt,
            cholesterolMilli = cholesterolMilli,
            caffeineMilli = caffeineMilli,
        )

    val vitamins =
        FoodYou3Vitamins(
            vitaminAMicro = vitaminAMicro,
            vitaminB1Milli = vitaminB1Milli,
            vitaminB2Milli = vitaminB2Milli,
            vitaminB3Milli = vitaminB3Milli,
            vitaminB5Milli = vitaminB5Milli,
            vitaminB6Milli = vitaminB6Milli,
            vitaminB7Micro = vitaminB7Micro,
            vitaminB9Micro = vitaminB9Micro,
            vitaminB12Micro = vitaminB12Micro,
            vitaminCMilli = vitaminCMilli,
            vitaminDMicro = vitaminDMicro,
            vitaminEMilli = vitaminEMilli,
            vitaminKMicro = vitaminKMicro,
        )

    val minerals =
        FoodYou3Minerals(
            manganeseMilli = manganeseMilli,
            magnesiumMilli = magnesiumMilli,
            potassiumMilli = potassiumMilli,
            calciumMilli = calciumMilli,
            copperMilli = copperMilli,
            zincMilli = zincMilli,
            sodiumMilli = sodiumMilli,
            ironMilli = ironMilli,
            phosphorusMilli = phosphorusMilli,
            seleniumMicro = seleniumMicro,
            iodineMicro = iodineMicro,
            chromiumMicro = chromiumMicro,
        )

    return FoodYou3FoodSearch(
        productId = productId,
        recipeId = recipeId,
        headline = headline,
        isLiquid = isLiquid,
        nutrients = nutrients,
        vitamins = vitamins,
        minerals = minerals,
        totalWeight = totalWeight,
        servingWeight = servingWeight,
        measurementType = measurementType,
        measurementValue = measurementValue,
    )
}

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
