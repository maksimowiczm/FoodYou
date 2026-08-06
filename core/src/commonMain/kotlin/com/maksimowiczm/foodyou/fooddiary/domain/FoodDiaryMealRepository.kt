package com.maksimowiczm.foodyou.fooddiary.domain

import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity

interface FoodDiaryMealRepository {
    suspend fun findEntriesUsing(identity: MealIdentity): List<FoodDiaryEntryIdentity>

    suspend fun saveReference(identity: FoodDiaryEntryIdentity, mealIdentity: MealIdentity)

    suspend fun removeReference(identity: FoodDiaryEntryIdentity)
}
