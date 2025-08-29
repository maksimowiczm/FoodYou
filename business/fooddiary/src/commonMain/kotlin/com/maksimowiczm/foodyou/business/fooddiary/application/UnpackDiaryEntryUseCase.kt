package com.maksimowiczm.foodyou.business.fooddiary.application

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.shared.application.database.TransactionProvider
import com.maksimowiczm.foodyou.business.shared.application.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate

sealed interface UnpackDiaryEntryError {
    data object EntryNotFound : UnpackDiaryEntryError

    data object MealNotFound : UnpackDiaryEntryError

    data object EntryCannotBeUnpacked : UnpackDiaryEntryError
}

fun interface UnpackDiaryEntryUseCase {
    suspend fun unpack(
        id: Long,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UnpackDiaryEntryError>
}

internal class UnpackDiaryEntryUseCaseImpl(
    private val diaryEntryRepository: DiaryEntryRepository,
    private val mealRepository: MealRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : UnpackDiaryEntryUseCase {
    override suspend fun unpack(
        id: Long,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UnpackDiaryEntryError> =
        transactionProvider.withTransaction {
            val entry = diaryEntryRepository.observeEntry(id).first()
            if (entry == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackDiaryEntryError.EntryNotFound,
                    message = { "Diary entry with id $id not found" },
                )
            }

            val food = entry.food
            if (food !is DiaryFoodRecipe) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackDiaryEntryError.EntryCannotBeUnpacked,
                    message = { "Diary entry with id $id cannot be unpacked" },
                )
            }

            val meal = mealRepository.observeMeal(mealId).firstOrNull()
            if (meal == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackDiaryEntryError.MealNotFound,
                    message = { "Meal with id $mealId not found" },
                )
            }

            // Replace the entry with unpacked entries
            diaryEntryRepository.deleteDiaryEntry(entry.id)

            val now = dateProvider.observeDateTime().first()
            val unpacked = food.unpack(measurement)
            unpacked.forEach {
                val entry =
                    DiaryEntry(
                        id = 0,
                        mealId = mealId,
                        date = date,
                        measurement = it.measurement,
                        food = it.food,
                        createdAt = entry.createdAt,
                        updatedAt = now,
                    )

                diaryEntryRepository.insertDiaryEntry(
                    mealId = entry.mealId,
                    date = entry.date,
                    measurement = entry.measurement,
                    food = entry.food,
                    createdAt = entry.createdAt,
                )
            }

            Ok(Unit)
        }

    private companion object {
        const val TAG = "UnpackDiaryEntryUseCaseImpl"
    }
}
