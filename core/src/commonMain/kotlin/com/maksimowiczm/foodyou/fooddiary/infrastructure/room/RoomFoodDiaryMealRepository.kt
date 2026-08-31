package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository
import com.maksimowiczm.foodyou.mealplan.domain.MealId

internal class RoomFoodDiaryMealRepository(private val dao: FoodDiaryMealDao) :
    FoodDiaryMealRepository {
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
