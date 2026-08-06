package com.maksimowiczm.foodyou.fooddiary.infrastructure.room

import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity

internal class RoomFoodDiaryMealRepository(private val dao: FoodDiaryMealDao) :
    FoodDiaryMealRepository {
    override suspend fun findEntriesUsing(identity: MealIdentity): List<FoodDiaryEntryIdentity> {
        return dao.findEntriesByMeal(identity.id).map(::FoodDiaryEntryIdentity)
    }

    override suspend fun saveReference(
        identity: FoodDiaryEntryIdentity,
        mealIdentity: MealIdentity,
    ) {
        dao.saveMealReference(
            FoodDiaryEntryMealEntity(
                entryId = identity.id,
                mealId = mealIdentity.id,
            )
        )
    }

    override suspend fun removeReference(identity: FoodDiaryEntryIdentity) {
        dao.deleteByEntryId(identity.id)
    }
}
