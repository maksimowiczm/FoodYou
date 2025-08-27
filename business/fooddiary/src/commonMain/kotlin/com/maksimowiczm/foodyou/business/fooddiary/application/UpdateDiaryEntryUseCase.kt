package com.maksimowiczm.foodyou.business.fooddiary.application

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate

sealed interface UpdateDiaryEntryError {
    data object EntryNotFound : UpdateDiaryEntryError

    data object InvalidMeasurement : UpdateDiaryEntryError

    data object MealNotFound : UpdateDiaryEntryError
}

fun interface UpdateDiaryEntryUseCase {
    suspend fun update(
        id: Long,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UpdateDiaryEntryError>
}

internal class UpdateDiaryEntryUseCaseImpl(
    private val mealRepository: MealRepository,
    private val diaryEntryRepository: DiaryEntryRepository,
    private val dateProvider: DateProvider,
    private val transactionProvider: DatabaseTransactionProvider,
    private val logger: Logger,
) : UpdateDiaryEntryUseCase {
    override suspend fun update(
        id: Long,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UpdateDiaryEntryError> =
        transactionProvider.withTransaction {
            val entry = diaryEntryRepository.observeEntry(id).first()

            if (entry == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateDiaryEntryError.EntryNotFound,
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
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Food must not be liquid for gram measurement" },
                        )
                    }

                is Measurement.Milliliter,
                is Measurement.FluidOunce ->
                    if (!entry.food.isLiquid) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Food must be liquid for milliliter measurement" },
                        )
                    }

                is Measurement.Package ->
                    if (entry.food.totalWeight == null) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Total weight must be provided for package measurement" },
                        )
                    }

                is Measurement.Serving ->
                    if (entry.food.servingWeight == null) {
                        return@withTransaction logger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Total weight must be provided for serving measurement" },
                        )
                    }
            }

            val meal = mealRepository.observeMeal(mealId).firstOrNull()

            if (meal == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateDiaryEntryError.MealNotFound,
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

            diaryEntryRepository.updateDiaryEntry(updated)
            Ok(Unit)
        }

    private companion object {
        const val TAG = "UpdateDiaryEntryUseCaseImpl"
    }
}
