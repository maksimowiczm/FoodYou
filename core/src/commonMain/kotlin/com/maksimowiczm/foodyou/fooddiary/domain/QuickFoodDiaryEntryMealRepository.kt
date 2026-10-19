package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.mealplan.domain.MealId

interface QuickFoodDiaryEntryMealRepository {
    suspend fun findEntriesUsing(mealId: MealId): List<QuickFoodDiaryEntryId>

    suspend fun saveReference(diaryEntryId: QuickFoodDiaryEntryId, mealId: MealId)

    suspend fun removeReference(entryId: QuickFoodDiaryEntryId)
}
