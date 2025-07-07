package com.maksimowiczm.foodyou.feature.fooddiary.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

@Dao
interface FoodDao {
    @Query(
        """
        SELECT 
            p.id AS productId, 
            NULL AS recipeId,
            CASE 
                WHEN p.brand IS NOT NULL THEN p.name || ' (' || p.brand || ')'
                ELSE p.name
            END AS headline,
            p.proteins,
            p.carbohydrates,
            p.fats,
            p.energy,
            p.saturatedFats,
            p.monounsaturatedFats,
            p.polyunsaturatedFats,
            p.omega3,
            p.omega6,
            p.sugars,
            p.salt,
            p.fiber,
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
            p.chromiumMicro
        FROM Product p
        ORDER BY headline ASC
        """
    )
    fun observeFood(): PagingSource<Int, Food>
}
