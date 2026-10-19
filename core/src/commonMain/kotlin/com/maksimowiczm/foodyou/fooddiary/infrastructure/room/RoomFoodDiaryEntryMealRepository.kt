package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.mealplan.domain.MealId

internal class RoomFoodDiaryEntryMealRepository(private val dao: FoodDiaryEntryMealDao) :
    FoodDiaryEntryMealRepository {
    override suspend fun findEntriesUsing(mealId: MealId): List<FoodDiaryEntryId> {
        return dao.findEntriesByMeal(mealId.value).map(::FoodDiaryEntryId)
    }

    override suspend fun saveReference(
        diaryEntryId: FoodDiaryEntryId,
        mealId: MealId,
    ) {
        dao.saveMealReference(
            FoodDiaryEntryMealEntity(
                entryId = diaryEntryId.id,
                mealId = mealId.value,
            )
        )
    }

    override suspend fun removeReference(entryId: FoodDiaryEntryId) {
        dao.deleteByEntryId(entryId.id)
    }
}
