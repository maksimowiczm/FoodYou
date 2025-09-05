package com.maksimowiczm.foodyou.business.fooddiary.application

import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate

sealed interface UpdateFoodDiaryEntryError {
    data object EntryNotFoundFood : UpdateFoodDiaryEntryError

    data object InvalidMeasurement : UpdateFoodDiaryEntryError

    data object MealNotFound : UpdateFoodDiaryEntryError
}

fun interface UpdateFoodDiaryEntryUseCase {
    suspend fun update(
        id: FoodDiaryEntryId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UpdateFoodDiaryEntryError>
}

internal class UpdateFoodDiaryEntryUseCaseImpl(
    private val mealRepository: MealRepository,
    private val entryRepository: FoodDiaryEntryRepository,
    private val dateProvider: DateProvider,
    private val transactionProvider: TransactionProvider,
    private val logger: Logger,
) : UpdateFoodDiaryEntryUseCase {
    override suspend fun update(
        id: FoodDiaryEntryId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UpdateFoodDiaryEntryError> =
        transactionProvider.withTransaction {
            val entry = entryRepository.observe(id).firstOrNull()

            if (entry == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateFoodDiaryEntryError.EntryNotFoundFood,
                    message = { "Diary entry with id $id not found" },
                )
            }

            when (measurement) {
                is Measurement.Gram,
                is Measurement.Ounce ->
                    if (entry.food.isLiquid) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateFoodDiaryEntryError.InvalidMeasurement,
                            message = { "Food must not be liquid for gram measurement" },
                        )
                    }

                is Measurement.Milliliter,
                is Measurement.FluidOunce ->
                    if (!entry.food.isLiquid) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateFoodDiaryEntryError.InvalidMeasurement,
                            message = { "Food must be liquid for milliliter measurement" },
                        )
                    }

                is Measurement.Package ->
                    if (entry.food.totalWeight == null) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateFoodDiaryEntryError.InvalidMeasurement,
                            message = { "Total weight must be provided for package measurement" },
                        )
                    }

                is Measurement.Serving ->
                    if (entry.food.servingWeight == null) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateFoodDiaryEntryError.InvalidMeasurement,
                            message = { "Total weight must be provided for serving measurement" },
                        )
                    }
            }

            val meal = mealRepository.observeMeal(mealId).firstOrNull()

            if (meal == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateFoodDiaryEntryError.MealNotFound,
                    message = { "Meal with ID $mealId not found" },
                )
            }

            val updated =
                entry.copy(
                    measurement = measurement,
                    mealId = mealId,
                    date = date,
                    updatedAt = dateProvider.now(),
                )

            entryRepository.update(updated)
            Ok(Unit)
        }

    private companion object {
        const val TAG = "UpdateFoodDiaryEntryUseCaseImpl"
    }
}
