package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

interface FoodDiaryDatabase {
    val foodDiaryEntryCompositionDao: FoodDiaryEntryCompositionDao
    val foodDiaryEntryMealDao: FoodDiaryEntryMealDao
    val quickFoodDiaryEntryMealDao: QuickFoodDiaryEntryMealDao
}
