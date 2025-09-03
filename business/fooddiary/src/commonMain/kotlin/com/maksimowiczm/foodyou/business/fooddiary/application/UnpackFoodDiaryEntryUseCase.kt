package com.maksimowiczm.foodyou.business.fooddiary.application

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryRepository
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

sealed interface UnpackFoodDiaryEntryError {
    data object EntryNotFoundFood : UnpackFoodDiaryEntryError

    data object MealNotFound : UnpackFoodDiaryEntryError

    data object EntryCannotBeUnpackedFood : UnpackFoodDiaryEntryError
}

fun interface UnpackFoodDiaryEntryUseCase {
    suspend fun unpack(
        id: FoodDiaryEntryId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UnpackFoodDiaryEntryError>
}

internal class UnpackFoodDiaryEntryUseCaseImpl(
    private val entryRepository: FoodDiaryEntryRepository,
    private val mealRepository: MealRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : UnpackFoodDiaryEntryUseCase {
    override suspend fun unpack(
        id: FoodDiaryEntryId,
        measurement: Measurement,
        mealId: Long,
        date: LocalDate,
    ): Result<Unit, UnpackFoodDiaryEntryError> =
        transactionProvider.withTransaction {
            val entry = entryRepository.observe(id).firstOrNull()
            if (entry == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackFoodDiaryEntryError.EntryNotFoundFood,
                    message = { "Diary entry with id $id not found" },
                )
            }

            val food = entry.food
            if (food !is DiaryFoodRecipe) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackFoodDiaryEntryError.EntryCannotBeUnpackedFood,
                    message = { "Diary entry with id $id cannot be unpacked" },
                )
            }

            val meal = mealRepository.observeMeal(mealId).firstOrNull()
            if (meal == null) {
                return@withTransaction logger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackFoodDiaryEntryError.MealNotFound,
                    message = { "Meal with id $mealId not found" },
                )
            }

            // Replace the entry with unpacked entries
            entryRepository.delete(entry.id)

            val now = dateProvider.observeDateTime().first()
            val unpacked = food.unpack(measurement)
            unpacked.forEach {
                val entry =
                    FoodDiaryEntry(
                        id = FoodDiaryEntryId(0),
                        mealId = mealId,
                        date = date,
                        measurement = it.measurement,
                        food = it.food,
                        createdAt = entry.createdAt,
                        updatedAt = now,
                    )

                entryRepository.insert(
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
        const val TAG = "UnpackFoodDiaryEntryUseCaseImpl"
    }
}
