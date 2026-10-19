package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.mealplan.domain.MealId

interface FoodDiaryEntryMealRepository {
    suspend fun findEntriesUsing(mealId: MealId): List<FoodDiaryEntryId>

    suspend fun saveReference(diaryEntryId: FoodDiaryEntryId, mealId: MealId)

    suspend fun removeReference(entryId: FoodDiaryEntryId)
}
