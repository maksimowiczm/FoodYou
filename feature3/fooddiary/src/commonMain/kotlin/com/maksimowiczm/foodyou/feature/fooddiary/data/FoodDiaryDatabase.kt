package com.maksimowiczm.foodyou.feature.fooddiary.data

interface FoodDiaryDatabase {
    val foodDao: FoodDao
    val mealDao: MealDao
    val measurementDao: MeasurementDao
}
