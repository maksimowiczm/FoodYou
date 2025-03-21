package com.maksimowiczm.foodyou.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.data.model.DailyGoals
import com.maksimowiczm.foodyou.data.model.DiaryDay
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.data.model.defaultGoals
import com.maksimowiczm.foodyou.data.model.toDomain
import com.maksimowiczm.foodyou.data.model.toEntity
import com.maksimowiczm.foodyou.data.preferences.DiaryPreferences
import com.maksimowiczm.foodyou.database.dao.AddFoodDao
import com.maksimowiczm.foodyou.database.entity.MealEntity
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class DiaryRepositoryImpl(
    private val addFoodDao: AddFoodDao,
    private val dataStore: DataStore<Preferences>
) : DiaryRepository {

    override fun observeDailyGoals(): Flow<DailyGoals> {
        val nutrientGoal = combine(
            dataStore.observe(DiaryPreferences.proteinsGoal),
            dataStore.observe(DiaryPreferences.carbohydratesGoal),
            dataStore.observe(DiaryPreferences.fatsGoal)
        ) { arr ->
            if (arr.any { it == null }) {
                return@combine null
            }

            arr.map { it!! }
        }

        return combine(
            dataStore.observe(DiaryPreferences.caloriesGoal),
            nutrientGoal
        ) { calories, nutrients ->
            if (nutrients == null || calories == null) {
                return@combine defaultGoals()
            }

            val (proteins, carbohydrates, fats) = nutrients

            DailyGoals(
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats
            )
        }
    }

    override fun observeDiaryDay(date: LocalDate): Flow<DiaryDay> {
        val epochDay = date.toEpochDays()

        return combine(
            addFoodDao.observeMeasuredProducts(
                mealId = null,
                epochDay = epochDay
            ),
            observeMeals(),
            observeDailyGoals()
        ) { products, meals, goals ->
            val mealProductMap = meals
                .associateWith { emptyList<ProductWithWeightMeasurement>() }
                .toMutableMap()

            products.forEach {
                val product = it.toDomain()
                val mealId = it.weightMeasurement.mealId
                val key = meals.firstOrNull { meal -> meal.id == mealId }

                if (key == null) {
                    Logger.e(TAG) { "Meal with id $mealId not found. Data inconsistency. BYE BYE" }
                    error("Meal with id $mealId not found. Data inconsistency. BYE BYE")
                }

                mealProductMap[key] = mealProductMap[key]!! + product
            }

            return@combine DiaryDay(
                date = date,
                mealProductMap = mealProductMap,
                dailyGoals = goals
            )
        }
    }

    override suspend fun setDailyGoals(goals: DailyGoals) {
        dataStore.set(
            DiaryPreferences.caloriesGoal to goals.calories,
            DiaryPreferences.proteinsGoal to goals.proteins,
            DiaryPreferences.carbohydratesGoal to goals.carbohydrates,
            DiaryPreferences.fatsGoal to goals.fats
        )
    }

    override fun observeMeals(): Flow<List<Meal>> = addFoodDao.observeMeals().map { list ->
        list.map(MealEntity::toDomain)
    }

    override fun observeMealById(id: Long) = addFoodDao.observeMealById(id).map { it?.toDomain() }

    override suspend fun createMeal(name: String, from: LocalTime, to: LocalTime) {
        addFoodDao.insertWithLastRank(
            MealEntity(
                name = name,
                fromHour = from.hour,
                fromMinute = from.minute,
                toHour = to.hour,
                toMinute = to.minute,
                rank = -1
            )
        )
    }

    override suspend fun updateMeal(meal: Meal) {
        addFoodDao.updateMeal(meal.toEntity())
    }

    override suspend fun deleteMeal(meal: Meal) {
        addFoodDao.deleteMeal(meal.toEntity())
    }

    override suspend fun updateMealsRanks(map: Map<Long, Int>) {
        addFoodDao.updateMealsRanks(map)
    }

    companion object {
        private const val TAG = "DiaryRepositoryImpl"
    }
}
