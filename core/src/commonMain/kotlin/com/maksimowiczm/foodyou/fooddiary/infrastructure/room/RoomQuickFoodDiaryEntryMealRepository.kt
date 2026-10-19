package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.mealplan.domain.MealId

internal class RoomQuickFoodDiaryEntryMealRepository(private val dao: QuickFoodDiaryEntryMealDao) :
    QuickFoodDiaryEntryMealRepository {
    override suspend fun findEntriesUsing(mealId: MealId): List<QuickFoodDiaryEntryId> {
        return dao.findEntriesByMeal(mealId.value).map(::QuickFoodDiaryEntryId)
    }

    override suspend fun saveReference(
        diaryEntryId: QuickFoodDiaryEntryId,
        mealId: MealId,
    ) {
        dao.saveMealReference(
            QuickFoodDiaryEntryMealEntity(
                entryId = diaryEntryId.id,
                mealId = mealId.value,
            )
        )
    }

    override suspend fun removeReference(entryId: QuickFoodDiaryEntryId) {
        dao.deleteByEntryId(entryId.id)
    }
}
