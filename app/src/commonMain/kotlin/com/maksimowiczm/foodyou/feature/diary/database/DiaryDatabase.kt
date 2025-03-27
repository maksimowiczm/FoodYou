package com.maksimowiczm.foodyou.feature.diary.database

import com.maksimowiczm.foodyou.feature.diary.database.dao.MealsDao

interface DiaryDatabase {
    val mealsDao: MealsDao
}
