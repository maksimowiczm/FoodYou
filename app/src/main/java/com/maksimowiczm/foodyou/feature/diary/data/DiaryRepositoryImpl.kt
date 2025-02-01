package com.maksimowiczm.foodyou.feature.diary.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.feature.addfood.data.model.toDomain
import com.maksimowiczm.foodyou.feature.addfood.data.model.toEntity
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDao
import com.maksimowiczm.foodyou.feature.addfood.database.AddFoodDatabase
import com.maksimowiczm.foodyou.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.defaultGoals
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
    addFoodDatabase: AddFoodDatabase,
    private val dataStore: DataStore<Preferences>,
    private val ioScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : DiaryRepository {
    private val addFoodDao: AddFoodDao = addFoodDatabase.addFoodDao()

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

    override fun observePortionsByMealDate(
        meal: Meal,
        date: LocalDate
    ): Flow<List<ProductWithWeightMeasurement>> {
        return addFoodDao.observeMeasuredProducts(
            epochDay = date.toEpochDay(),
            mealId = meal.toEntity().value
        ).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeDiaryDay(date: LocalDate): Flow<DiaryDay> {
        val epochDay = date.toEpochDay()

        return combine(
            addFoodDao.observeMeasuredProducts(
                mealId = null,
                epochDay = epochDay
            ),
            observeCurrentGoals()
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
}
