package com.maksimowiczm.foodyou.feature.diary.database

import com.maksimowiczm.foodyou.feature.diary.database.meal.MealDao

internal interface DiaryDatabase {
    val mealDao: MealDao
}
