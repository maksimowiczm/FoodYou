package com.maksimowiczm.foodyou.business.fooddiary.application

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.domain.food.FoodId
import com.maksimowiczm.foodyou.business.shared.domain.fooddiary.FoodDiaryEntryCreatedDomainEvent
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

sealed interface CreateFoodDiaryEntryError {
    data object MealNotFound : CreateFoodDiaryEntryError

    data object InvalidMeasurement : CreateFoodDiaryEntryError
}

fun interface CreateFoodDiaryEntryUseCase {
    suspend fun createDiaryEntry(
        foodId: FoodId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
    ): Result<FoodDiaryEntryId, CreateFoodDiaryEntryError>
}

internal class CreateFoodDiaryEntryUseCaseImpl(
    private val mealRepository: MealRepository,
    private val entryRepository: FoodDiaryEntryRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
    private val eventBus: EventBus,
    private val logger: Logger,
) : CreateFoodDiaryEntryUseCase {
    override suspend fun createDiaryEntry(
        foodId: FoodId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
    ): Result<FoodDiaryEntryId, CreateFoodDiaryEntryError> {
        when (measurement) {
            is Measurement.Gram,
            is Measurement.Ounce ->
                if (food.isLiquid) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateFoodDiaryEntryError.InvalidMeasurement,
                        message = { "Food must not be liquid for gram measurement" },
                    )
                }

            is Measurement.Milliliter,
            is Measurement.FluidOunce ->
                if (!food.isLiquid) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateFoodDiaryEntryError.InvalidMeasurement,
                        message = { "Food must be liquid for milliliter measurement" },
                    )
                }

            is Measurement.Package ->
                if (food.totalWeight == null) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateFoodDiaryEntryError.InvalidMeasurement,
                        message = { "Total weight must be provided for package measurement" },
                    )
                }

            is Measurement.Serving ->
                if (food.servingWeight == null) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateFoodDiaryEntryError.InvalidMeasurement,
                        message = { "Total weight must be provided for serving measurement" },
                    )
                }
        }

        val now = dateProvider.now()
        return transactionProvider
            .withTransaction {
                val meal = mealRepository.observeMeal(mealId).first()

                if (meal == null) {
                    return@withTransaction logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateFoodDiaryEntryError.MealNotFound,
                        message = { "Meal with id $mealId not found" },
                    )
                }

                val entryId =
                    entryRepository.insert(
                        measurement = measurement,
                        mealId = mealId,
                        date = date,
                        food = food,
                        createdAt = now,
                    )

                Ok(entryId)
            }
            .onSuccess { id ->
                eventBus.publish(
                    FoodDiaryEntryCreatedDomainEvent(
                        foodId = foodId,
                        date = now,
                        measurement = measurement,
                    )
                )
            }
    }

    private companion object {
        const val TAG = "CreateDiaryEntryUseCaseImpl"
    }
}
