package com.maksimowiczm.foodyou.feature.fooddiary.data

interface FoodDiaryDatabase {
    val mealDao: MealDao
    val measurementDao: MeasurementDao
}
