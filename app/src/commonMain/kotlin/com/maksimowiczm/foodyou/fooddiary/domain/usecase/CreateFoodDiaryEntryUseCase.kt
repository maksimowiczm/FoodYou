package com.maksimowiczm.foodyou.fooddiary.domain.usecase

import com.maksimowiczm.foodyou.common.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.common.log.Logger
import com.maksimowiczm.foodyou.common.log.logAndReturnFailure
import com.maksimowiczm.foodyou.common.result.Ok
import com.maksimowiczm.foodyou.common.result.Result
import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFood
import com.maksimowiczm.foodyou.fooddiary.domain.entity.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

sealed interface CreateFoodDiaryEntryError {
    data object MealNotFound : CreateFoodDiaryEntryError

    data object InvalidMeasurement : CreateFoodDiaryEntryError
}

class CreateFoodDiaryEntryUseCase(
    private val mealRepository: MealRepository,
    private val entryRepository: FoodDiaryEntryRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) {
    suspend fun createDiaryEntry(
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
        return transactionProvider.withTransaction {
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
    }

    private companion object {
        const val TAG = "CreateFoodDiaryEntryUseCase"
    }
}
