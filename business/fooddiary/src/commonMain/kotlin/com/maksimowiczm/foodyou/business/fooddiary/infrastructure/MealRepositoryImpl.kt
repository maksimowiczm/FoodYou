package com.maksimowiczm.foodyou.business.fooddiary.infrastructure

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreMealsPreferencesDataStore
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomMealDataSource
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalTime

internal class MealRepositoryImpl(
    private val localMealDataSource: RoomMealDataSource,
    private val localMealsPreferencesDataSource: DataStoreMealsPreferencesDataStore,
    private val logger: Logger,
) : MealRepository {
    override fun observeMeal(mealId: Long): Flow<Meal?> =
        localMealDataSource.observeMealById(mealId)

    override fun observeMeals(): Flow<List<Meal>> = localMealDataSource.observeAllMeals()

    override suspend fun createMealWithLastRank(name: String, from: LocalTime, to: LocalTime) {
        if (name.isBlank()) {
            logger.w(TAG) { "Trying to create meal with blank name. Operation aborted." }
            return
        }

        val meal = Meal(id = 0, name = name, from = from, to = to, rank = 0)
        localMealDataSource.insertWithLastRank(meal)
    }

    override suspend fun deleteMeal(mealId: Long) {
        val meal = localMealDataSource.observeMealById(mealId).first()

        if (meal == null) {
            logger.w(TAG) { "Trying to delete meal with id $mealId, but it does not exist." }
            return
        }

        localMealDataSource.delete(meal)
    }

    override suspend fun updateMeal(id: Long, name: String, from: LocalTime, to: LocalTime) {
        if (name.isBlank()) {
            logger.w(TAG) { "Trying to update meal with blank name. Operation aborted." }
            return
        }

        val meal = localMealDataSource.observeMealById(id).first()
        if (meal == null) {
            logger.w(TAG) { "Trying to update meal with id $id, but it does not exist." }
            return
        }

        val updatedMeal = meal.copy(name = name, from = from, to = to)
        localMealDataSource.update(updatedMeal)
    }

    override suspend fun reorderMeals(order: List<Long>) {
        localMealDataSource.reorder(order)
    }

    override fun observeMealsPreferences(): Flow<MealsPreferences> =
        localMealsPreferencesDataSource.observe()

    override suspend fun updateMealsPreferences(
        preferences: MealsPreferences.() -> MealsPreferences
    ) {
        localMealsPreferencesDataSource.update(preferences)
    }

    private companion object {
        const val TAG = "MealRepositoryImpl"
    }
}
