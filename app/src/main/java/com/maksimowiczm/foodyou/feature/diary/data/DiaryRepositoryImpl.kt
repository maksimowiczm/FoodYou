package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.toDomain
import com.maksimowiczm.foodyou.feature.diary.database.DiaryDao
import com.maksimowiczm.foodyou.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.infrastructure.datastore.set
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

internal class DiaryRepositoryImpl(
    private val diaryDao: DiaryDao,
    private val dataStore: DataStore<Preferences>,
    private val ioScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : DiaryRepository {
    override fun getSelectedDate(): LocalDate = runBlocking(ioScope.coroutineContext) {
        dataStore
            .observe(DiaryPreferences.selectedDateEpoch)
            .map {
                val date = it?.let { LocalDate.ofEpochDay(it) }

                date ?: LocalDate.now()
            }
            .first()
    }

    override suspend fun setSelectedDate(date: LocalDate) {
        dataStore.set(DiaryPreferences.selectedDateEpoch to date.toEpochDay())
    }

    private fun observeCurrentGoals(): Flow<DailyGoals> {
        val mealCaloriesGoals = combine(
            dataStore.observe(DiaryPreferences.breakfastCalories),
            dataStore.observe(DiaryPreferences.lunchCalories),
            dataStore.observe(DiaryPreferences.dinnerCalories),
            dataStore.observe(DiaryPreferences.snacksCalories)
        ) { arr ->
            if (arr.any { it == null }) {
                return@combine null
            }

            val (breakfast, lunch, dinner, snacks) = arr.map { it!! }

            mapOf(
                Meal.Breakfast to breakfast,
                Meal.Lunch to lunch,
                Meal.Dinner to dinner,
                Meal.Snacks to snacks
            )
        }

        val nutrimentGoals = combine(
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
            mealCaloriesGoals,
            dataStore.observe(DiaryPreferences.caloriesGoal),
            nutrimentGoals
        ) { mealCalories, calories, nutriments ->
            if (mealCalories == null || nutriments == null || calories == null) {
                return@combine defaultGoals()
            }

            val (proteins, carbohydrates, fats) = nutriments

            DailyGoals(
                calories = calories,
                proteins = proteins,
                carbohydrates = carbohydrates,
                fats = fats,
                mealCalorieGoalMap = mealCalories
            )
        }
    }

    override fun observeDiaryDay(date: LocalDate): Flow<DiaryDay> {
        val epochDay = date.toEpochDay()

        return combine(
            diaryDao.observeMealProducts(epochDay),
            observeCurrentGoals()
        ) { products, goals ->

            val meals = products
                .groupBy { it.mealProduct.mealId }
                .map { (mealId, products) ->
                    mealId.toDomain() to products.map { it.toDomain() }
                }
                .toMap()

            return@combine DiaryDay(
                date = date,
                mealProducts = meals,
                dailyGoals = goals
            )
        }
    }
}
