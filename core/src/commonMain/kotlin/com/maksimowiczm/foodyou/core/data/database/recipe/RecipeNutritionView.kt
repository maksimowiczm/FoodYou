package com.maksimowiczm.foodyou.core.data.database.recipe

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.GRAM
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.PACKAGE
import com.maksimowiczm.foodyou.core.data.database.measurement.MeasurementSQLConstants.SERVING
import com.maksimowiczm.foodyou.core.data.model.Minerals
import com.maksimowiczm.foodyou.core.data.model.Nutrients
import com.maksimowiczm.foodyou.core.data.model.Vitamins

@DatabaseView(
    """
    SELECT 
        r.id AS recipeId,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.calories * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.calories * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.calories * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS calories,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.proteins * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.proteins * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS proteins,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.carbohydrates * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.carbohydrates * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS carbohydrates,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.fats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.fats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.fats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS fats,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.saturatedFats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.saturatedFats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.saturatedFats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS saturatedFats,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.monounsaturatedFats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.monounsaturatedFats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.monounsaturatedFats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS monounsaturatedFats,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.polyunsaturatedFats * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.polyunsaturatedFats * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.polyunsaturatedFats * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS polyunsaturatedFats,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.omega3 * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.omega3 * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.omega3 * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS omega3,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.omega6 * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.omega6 * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.omega6 * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS omega6,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.sugars * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.sugars * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.sugars * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS sugars,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.salt * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.salt * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.salt * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS salt,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.fiber * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.fiber * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.fiber * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS fiber,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.cholesterolMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.cholesterolMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.cholesterolMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS cholesterolMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.caffeineMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.caffeineMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.caffeineMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS caffeineMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminAMicro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminAMicro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminAMicro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminAMicro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB1Milli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB1Milli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB1Milli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB1Milli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB2Milli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB2Milli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB2Milli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB2Milli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB3Milli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB3Milli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB3Milli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB3Milli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB5Milli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB5Milli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB5Milli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB5Milli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB6Milli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB6Milli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB6Milli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB6Milli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB7Micro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB7Micro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB7Micro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB7Micro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB9Micro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB9Micro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB9Micro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB9Micro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminB12Micro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminB12Micro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminB12Micro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminB12Micro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminCMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminCMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminCMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminCMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminDMicro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminDMicro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminDMicro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminDMicro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminEMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminEMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminEMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminEMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.vitaminKMicro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.vitaminKMicro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.vitaminKMicro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS vitaminKMicro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.manganeseMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.manganeseMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.manganeseMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS manganeseMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.magnesiumMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.magnesiumMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.magnesiumMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS magnesiumMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.potassiumMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.potassiumMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.potassiumMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS potassiumMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.calciumMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.calciumMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.calciumMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS calciumMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.copperMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.copperMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.copperMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS copperMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.zincMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.zincMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.zincMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS zincMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.sodiumMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.sodiumMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.sodiumMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS sodiumMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.ironMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.ironMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.ironMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS ironMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.phosphorusMilli * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.phosphorusMilli * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.phosphorusMilli * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS phosphorusMilli,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.seleniumMicro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.seleniumMicro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.seleniumMicro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS seleniumMicro,
        SUM(
            CASE 
                WHEN ri.measurement = $GRAM THEN p.iodineMicro * (ri.quantity / 100.0)
                WHEN ri.measurement = $PACKAGE AND p.packageWeight IS NOT NULL THEN p.iodineMicro * (ri.quantity * p.packageWeight / 100.0)
                WHEN ri.measurement = $SERVING AND p.servingWeight IS NOT NULL THEN p.iodineMicro * (ri.quantity * p.servingWeight / 100.0)
                ELSE 0 
            END
        ) * 100 / rw.totalWeight AS iodineMicro
    FROM RecipeEntity r
    JOIN RecipeIngredientEntity ri ON r.id = ri.recipeId
    JOIN ProductEntity p ON ri.productId = p.id
    JOIN RecipeWeightView rw ON r.id = rw.recipeId
    GROUP BY r.id
    """
)
data class RecipeNutritionView(
    val recipeId: Long,

    // Total nutrition per 100g
    @Embedded
    val nutrition: Nutrients,

    @Embedded
    val vitamins: Vitamins,

    @Embedded
    val minerals: Minerals
)
