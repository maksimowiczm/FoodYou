package com.maksimowiczm.foodyou.business.fooddiary.application

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.domain.fooddiary.FoodDiaryEntryCreatedDomainEvent
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

sealed interface CreateDiaryEntryError {
    data object MealNotFound : CreateDiaryEntryError

    data object InvalidMeasurement : CreateDiaryEntryError
}

fun interface CreateDiaryEntryUseCase {
    suspend fun createDiaryEntry(
        foodId: FoodId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
    ): Result<Long, CreateDiaryEntryError>
}

internal class CreateDiaryEntryUseCaseImpl(
    private val mealRepository: MealRepository,
    private val diaryEntryRepository: DiaryEntryRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
    private val eventBus: EventBus,
    private val logger: Logger,
) : CreateDiaryEntryUseCase {
    override suspend fun createDiaryEntry(
        foodId: FoodId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
        food: DiaryFood,
    ): Result<Long, CreateDiaryEntryError> {
        when (measurement) {
            is Measurement.Gram,
            is Measurement.Ounce ->
                if (food.isLiquid) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Food must not be liquid for gram measurement" },
                    )
                }

            is Measurement.Milliliter,
            is Measurement.FluidOunce ->
                if (!food.isLiquid) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Food must be liquid for milliliter measurement" },
                    )
                }

            is Measurement.Package ->
                if (food.totalWeight == null) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Total weight must be provided for package measurement" },
                    )
                }

            is Measurement.Serving ->
                if (food.servingWeight == null) {
                    return logger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
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
                        error = CreateDiaryEntryError.MealNotFound,
                        message = { "Meal with id $mealId not found" },
                    )
                }

                val entryId =
                    diaryEntryRepository.insertDiaryEntry(
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
                        entryId = id,
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
