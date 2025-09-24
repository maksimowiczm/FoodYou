package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

interface FoodDiaryDatabase {
    val manualDiaryEntryDao: ManualDiaryEntryDao
    val measurementDao: MeasurementDao
    val mealDao: MealDao
}
