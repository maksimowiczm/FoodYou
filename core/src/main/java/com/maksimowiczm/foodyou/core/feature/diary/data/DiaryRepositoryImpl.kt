package com.maksimowiczm.foodyou.core.feature.diary.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.toDomain
import com.maksimowiczm.foodyou.core.feature.addfood.database.AddFoodDao
import com.maksimowiczm.foodyou.core.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.core.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.core.infrastructure.datastore.observe
import com.maksimowiczm.foodyou.core.infrastructure.datastore.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate

class DiaryRepositoryImpl(
    addFoodDatabase: AddFoodDatabase,
    private val dataStore: DataStore<Preferences>
) : DiaryRepository {
    private val addFoodDao: AddFoodDao = addFoodDatabase.addFoodDao()

    override fun observeDailyGoals(): Flow<DailyGoals> {
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
            dataStore.observe(DiaryPreferences.caloriesGoal),
            nutrimentGoals
        ) { calories, nutriments ->
            if (nutriments == null || calories == null) {
                return@combine defaultGoals()
            }

            val (proteins, carbohydrates, fats) = nutriments

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
            observeDailyGoals()
        ) { products, goals ->
            val meals = products
                .groupBy { it.weightMeasurement.mealId }
                .mapKeys { (mealId, _) ->
                    mealId.toDomain()
                }
                .mapValues { (_, products) ->
                    products.map { it.toDomain() }
                }

            return@combine DiaryDay(
                date = date,
                mealProductMap = meals,
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
}
