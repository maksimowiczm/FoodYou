package com.maksimowiczm.foodyou.feature.goals.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.data.model.diaryday.DiaryDay as DbDiaryDay
import com.maksimowiczm.foodyou.core.domain.mapper.MeasurementMapper
import com.maksimowiczm.foodyou.core.domain.mapper.NutritionFactsMapper
import com.maksimowiczm.foodyou.core.domain.model.DailyGoals
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.model.defaultGoals
import com.maksimowiczm.foodyou.core.domain.source.DiaryDayLocalDataSource
import com.maksimowiczm.foodyou.core.ext.observe
import com.maksimowiczm.foodyou.core.ext.set
import com.maksimowiczm.foodyou.feature.goals.model.DiaryDay
import com.maksimowiczm.foodyou.feature.goals.model.Food
import com.maksimowiczm.foodyou.feature.goals.model.Meal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

internal class GoalsRepository(
    private val dataStore: DataStore<Preferences>,
    private val diaryDayDao: DiaryDayLocalDataSource
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeDiaryDay(date: LocalDate) = diaryDayDao
        .observeDiaryDay(epochDay = date.toEpochDays())
        .flatMapLatest { diaryDayView ->
            val foods = diaryDayView.toFoods()

            observeDailyGoals().map {
                DiaryDay(
                    date = date,
                    foods = foods,
                    dailyGoals = it
                )
            }
        }

    fun observeDailyGoals(): Flow<DailyGoals> {
        val nutrientGoal = combine(
            dataStore.observe(GoalsPreferences.proteinsGoal),
            dataStore.observe(GoalsPreferences.carbohydratesGoal),
            dataStore.observe(GoalsPreferences.fatsGoal)
        ) { arr ->
            if (arr.any { it == null }) {
                return@combine null
            }

            arr.map { it!! }
        }

        return combine(
            dataStore.observe(GoalsPreferences.caloriesGoal),
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

    suspend fun setDailyGoals(goals: DailyGoals) {
        dataStore.set(
            GoalsPreferences.caloriesGoal to goals.calories,
            GoalsPreferences.proteinsGoal to goals.proteins,
            GoalsPreferences.carbohydratesGoal to goals.carbohydrates,
            GoalsPreferences.fatsGoal to goals.fats
        )
    }
}

private fun List<DbDiaryDay>.toFoods(): Map<Meal, List<Food>> = groupBy {
    Meal(
        id = it.mealId,
        name = it.mealName
    )
}.mapValues { (_, list) ->
    list.map {
        val productId = it.productId
        val recipeId = it.recipeId

        val id = when {
            productId != null -> FoodId.Product(productId)
            recipeId != null -> FoodId.Recipe(recipeId)
            else -> error("Product ID and Recipe ID are both null")
        }

        Food(
            foodId = id,
            name = it.foodBrand?.let { brand -> it.foodName + " ($brand)" } ?: it.foodName,
            packageWeight = it.packageWeight?.let { PortionWeight.Package(it) },
            servingWeight = it.servingWeight?.let { PortionWeight.Serving(it) },
            nutrients = NutritionFactsMapper.toNutritionFacts(
                it.nutrients,
                it.vitamins,
                it.minerals
            ),
            measurement = with(MeasurementMapper) { it.toMeasurement() }
        )
    }
}
