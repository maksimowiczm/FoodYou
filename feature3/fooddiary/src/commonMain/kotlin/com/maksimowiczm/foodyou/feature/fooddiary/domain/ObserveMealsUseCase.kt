package com.maksimowiczm.foodyou.feature.fooddiary.domain

import com.maksimowiczm.foodyou.feature.food.domain.FoodId
import com.maksimowiczm.foodyou.feature.food.domain.ObserveRecipeUseCase
import com.maksimowiczm.foodyou.feature.food.domain.ProductMapper
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodWithMeasurement as FoodWithMeasurementEntity
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

internal interface ObserveMealsUseCase {
    operator fun invoke(date: LocalDate): Flow<List<Meal>>
}

internal class ObserveMealsUseCaseImpl(
    foodDiaryDatabase: FoodDiaryDatabase,
    private val observeRecipeUseCase: ObserveRecipeUseCase,
    private val productMapper: ProductMapper
) : ObserveMealsUseCase {
    private val mealDao = foodDiaryDatabase.mealDao
    private val measurementDao = foodDiaryDatabase.measurementDao

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(date: LocalDate): Flow<List<Meal>> {
        return mealDao.observeMeals().flatMapLatest { meals ->
            if (meals.isEmpty()) {
                return@flatMapLatest flowOf(emptyList())
            }

            meals.map { meal ->
                measurementDao.observeFoodWithMeasurement(
                    mealId = meal.id,
                    epochDay = date.toEpochDays()
                ).flatMapLatest { food ->
                    val from = LocalTime(meal.fromHour, meal.fromMinute)
                    val to = LocalTime(meal.toHour, meal.toMinute)

                    if (food.isEmpty()) {
                        flowOf(
                            Meal(
                                id = meal.id,
                                name = meal.name,
                                from = from,
                                to = to,
                                rank = meal.rank,
                                food = emptyList()
                            )
                        )
                    } else {
                        food.map { it.toFood() }.combine().map { food ->
                            Meal(
                                id = meal.id,
                                name = meal.name,
                                from = from,
                                to = to,
                                rank = meal.rank,
                                food = food
                            )
                        }
                    }
                }
            }.combine()
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
