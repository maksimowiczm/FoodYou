package com.maksimowiczm.foodyou.core.data.database.goals

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.data.model.Minerals
import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.Vitamins
import com.maksimowiczm.foodyou.core.data.model.diaryday.DiaryDay
import com.maksimowiczm.foodyou.core.data.model.measurement.Measurement

@DatabaseView(
    """
    WITH Product AS (
        SELECT
            diaryEpochDay AS epochDay,
            pm.mealId AS mealId,
            m.name AS mealName,
            pm.productId AS productId,
            NULL AS recipeId,
            p.name AS foodName,
            p.brand AS foodBrand,
            p.proteins AS proteins,
            p.carbohydrates AS carbohydrates,
            p.fats AS fats,
            p.calories AS calories,
            p.saturatedFats AS saturatedFats,
            p.monounsaturatedFats AS monounsaturatedFats,
            p.polyunsaturatedFats AS polyunsaturatedFats,
            p.omega3 AS omega3,
            p.omega6 AS omega6,
            p.sugars AS sugars,
            p.salt AS salt,
            p.fiber AS fiber,
            p.cholesterolMilli AS cholesterolMilli,
            p.caffeineMilli AS caffeineMilli,
            p.vitaminAMicro AS vitaminAMicro,
            p.vitaminB1Milli AS vitaminB1Milli,
            p.vitaminB2Milli AS vitaminB2Milli,
            p.vitaminB3Milli AS vitaminB3Milli,
            p.vitaminB5Milli AS vitaminB5Milli,
            p.vitaminB6Milli AS vitaminB6Milli,
            p.vitaminB7Micro AS vitaminB7Micro,
            p.vitaminB9Micro AS vitaminB9Micro,
            p.vitaminB12Micro AS vitaminB12Micro,
            p.vitaminCMilli AS vitaminCMilli,
            p.vitaminDMicro AS vitaminDMicro,
            p.vitaminEMilli AS vitaminEMilli,
            p.vitaminKMicro AS vitaminKMicro,
            p.manganeseMilli AS manganeseMilli,
            p.magnesiumMilli AS magnesiumMilli,
            p.potassiumMilli AS potassiumMilli,
            p.calciumMilli AS calciumMilli,
            p.copperMilli AS copperMilli,
            p.zincMilli AS zincMilli,
            p.sodiumMilli AS sodiumMilli,
            p.ironMilli AS ironMilli,
            p.phosphorusMilli AS phosphorusMilli,
            p.seleniumMicro AS seleniumMicro,
            p.iodineMicro AS iodineMicro,
            p.packageWeight AS packageWeight,
            p.servingWeight AS servingWeight,
            pm.id AS measurementId,
            pm.measurement AS measurement,
            pm.quantity AS quantity
        FROM ProductMeasurementEntity pm
        JOIN ProductEntity p ON pm.productId = p.id
        JOIN MealEntity m ON pm.mealId = m.id
        WHERE pm.isDeleted = 0
    ),
    Recipe AS (
        SELECT
            epochDay AS epochDay,
            rm.mealId AS mealId,
            m.name AS mealName,
            NULL AS productId,
            r.id AS recipeId,
            r.name AS foodName,
            NULL AS foodBrand,
            rn.proteins AS proteins,
            rn.carbohydrates AS carbohydrates,
            rn.fats AS fats,
            rn.calories AS calories,
            rn.saturatedFats AS saturatedFats,
            rn.monounsaturatedFats AS monounsaturatedFats,
            rn.polyunsaturatedFats AS polyunsaturatedFats,
            rn.omega3 AS omega3,
            rn.omega6 AS omega6,
            rn.sugars AS sugars,
            rn.salt AS salt,
            rn.fiber AS fiber,
            rn.cholesterolMilli AS cholesterolMilli,
            rn.caffeineMilli AS caffeineMilli,
            rn.vitaminAMicro AS vitaminAMicro,
            rn.vitaminB1Milli AS vitaminB1Milli,
            rn.vitaminB2Milli AS vitaminB2Milli,
            rn.vitaminB3Milli AS vitaminB3Milli,
            rn.vitaminB5Milli AS vitaminB5Milli,
            rn.vitaminB6Milli AS vitaminB6Milli,
            rn.vitaminB7Micro AS vitaminB7Micro,
            rn.vitaminB9Micro AS vitaminB9Micro,
            rn.vitaminB12Micro AS vitaminB12Micro,
            rn.vitaminCMilli AS vitaminCMilli,
            rn.vitaminDMicro AS vitaminDMicro,
            rn.vitaminEMilli AS vitaminEMilli,
            rn.vitaminKMicro AS vitaminKMicro,
            rn.manganeseMilli AS manganeseMilli,
            rn.magnesiumMilli AS magnesiumMilli,
            rn.potassiumMilli AS potassiumMilli,
            rn.calciumMilli AS calciumMilli,
            rn.copperMilli AS copperMilli,
            rn.zincMilli AS zincMilli,
            rn.sodiumMilli AS sodiumMilli,
            rn.ironMilli AS ironMilli,
            rn.phosphorusMilli AS phosphorusMilli,
            rn.seleniumMicro AS seleniumMicro,
            rn.iodineMicro AS iodineMicro,
            rw.totalWeight AS packageWeight,
            rw.servingWeight AS servingWeight,
            rm.id AS measurementId,
            rm.measurement AS measurement,
            rm.quantity AS quantity
        FROM RecipeMeasurementEntity rm
        JOIN RecipeEntity r ON rm.recipeId = r.id
        JOIN RecipeNutritionView rn ON r.id = rn.recipeId
        JOIN RecipeWeightView rw ON r.id = rw.recipeId
        JOIN MealEntity m ON rm.mealId = m.id
        WHERE rm.isDeleted = 0
    )
    SELECT * FROM Product
    UNION ALL
    SELECT * FROM Recipe
    """
)
data class DiaryDayView(
    // Date
    override val epochDay: Long,

    // Meal
    override val mealId: Long,
    override val mealName: String,

    // Food
    override val productId: Long?,
    override val recipeId: Long?,
    override val foodName: String,
    override val foodBrand: String?,
    @Embedded
    override val nutrients: Nutrients,
    @Embedded
    override val vitamins: Vitamins,
    @Embedded
    override val minerals: Minerals,

    override val packageWeight: Float?,
    override val servingWeight: Float?,

    // Measurement
    override val measurementId: Long,
    override val measurement: Measurement,
    override val quantity: Float
) : DiaryDay
