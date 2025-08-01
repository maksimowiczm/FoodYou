package com.maksimowiczm.foodyou.feature.fooddiary.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.util.DateProvider
import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodWithMeasurement as FoodWithMeasurementEntity
import com.maksimowiczm.foodyou.feature.fooddiary.data.from
import com.maksimowiczm.foodyou.feature.fooddiary.data.to
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.IgnoreAllDayMeals
import com.maksimowiczm.foodyou.feature.fooddiary.preferences.UseTimeBasedSorting
import com.maksimowiczm.foodyou.feature.measurement.domain.Measurement
import com.maksimowiczm.foodyou.feature.measurement.domain.from
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun interface ObserveMealsUseCase {
    fun observe(date: LocalDate): Flow<List<Meal>>
    operator fun invoke(date: LocalDate): Flow<List<Meal>> = observe(date)
}

internal class ObserveMealsUseCaseImpl(
    foodDiaryDatabase: FoodDiaryDatabase,
    private val observeRecipeUseCase: ObserveRecipeUseCase,
    private val productMapper: ProductMapper,
    dataStore: DataStore<Preferences>,
    private val dateProvider: DateProvider
) : ObserveMealsUseCase {
    private val mealDao = foodDiaryDatabase.mealDao
    private val measurementDao = foodDiaryDatabase.measurementDao

    private val useTimeBasedSorting = dataStore.userPreference<UseTimeBasedSorting>()
    private val ignoreAllDayMeals = dataStore.userPreference<IgnoreAllDayMeals>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observe(date: LocalDate): Flow<List<Meal>> {
        return mealDao.observeMeals().flatMapLatest { meals ->
            if (meals.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            meals.map { meal ->
                measurementDao.observeFoodWithMeasurement(
                    mealId = meal.id,
                    epochDay = date.toEpochDays()
                ).flatMapLatest { food ->
                    if (food.isEmpty()) {
                        flowOf(
                            Meal(
                                id = meal.id,
                                name = meal.name,
                                from = meal.from,
                                to = meal.to,
                                rank = meal.rank,
                                food = emptyList()
                            )
                        )
                    } else {
                        food.map { it.toFood() }.combine().map { food ->
                            Meal(
                                id = meal.id,
                                name = meal.name,
                                from = meal.from,
                                to = meal.to,
                                rank = meal.rank,
                                food = food
                            )
                        }
                    }
                }
            }.combine().flatMapLatest { meals ->
                combine(
                    ignoreAllDayMeals.observe(),
                    useTimeBasedSorting.observe(),
                    dateProvider.observeMinutes()
                ) { ignoreAllDayMeals, timeBased, time ->
                    meals.sortedBy { meal ->
                        if (timeBased) {
                            if (shouldShowMeal(meal, time, ignoreAllDayMeals)) {
                                meal.rank
                            } else {
                                1_000_000 + meal.rank
                            }
                        } else {
                            meal.rank
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun FoodWithMeasurementEntity.toFood(): Flow<FoodWithMeasurement> {
        val date = Instant
            .fromEpochSeconds(measurement.createdAt)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

        return when {
            product != null -> flowOf(
                FoodWithMeasurement(
                    measurementId = measurement.id,
                    measurement = Measurement.from(
                        measurement.measurement,
                        measurement.quantity
                    ),
                    measurementDate = date,
                    mealId = measurement.mealId,
                    food = productMapper.toModel(product)
                )
            )

            recipe != null -> observeRecipeUseCase(FoodId.Recipe(recipe.id))
                .filterNotNull()
                .map { recipeModel ->
                    FoodWithMeasurement(
                        measurementId = measurement.id,
                        measurement = Measurement.from(
                            measurement.measurement,
                            measurement.quantity
                        ),
                        measurementDate = date,
                        mealId = measurement.mealId,
                        food = recipeModel
                    )
                }

            else -> error("FoodWithMeasurement must have either product or recipe")
        }
    }
}

private fun shouldShowMeal(meal: Meal, time: LocalTime, ignoreAllDayMeals: Boolean): Boolean =
    if (meal.isAllDay) {
        !ignoreAllDayMeals
    } else if (meal.to < meal.from) {
        val minuteBeforeMidnight = LocalTime(23, 59, 59)
        val midnight = LocalTime(0, 0, 0)
        meal.from <= time && time <= minuteBeforeMidnight || midnight <= time && time <= meal.to
    } else {
        meal.from <= time && time <= meal.to
    }
