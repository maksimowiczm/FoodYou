package com.maksimowiczm.foodyou.fooddiary.domain.usecase

import com.maksimowiczm.foodyou.fooddiary.domain.entity.DiaryFoodRecipe
import com.maksimowiczm.foodyou.fooddiary.domain.entity.FoodDiaryEntry
import com.maksimowiczm.foodyou.fooddiary.domain.entity.FoodDiaryEntryId
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.shared.domain.Ok
import com.maksimowiczm.foodyou.shared.domain.Result
import com.maksimowiczm.foodyou.shared.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import com.maksimowiczm.foodyou.shared.domain.log.Logger
import com.maksimowiczm.foodyou.shared.domain.log.logAndReturnFailure
import com.maksimowiczm.foodyou.shared.domain.measurement.Measurement
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate

sealed interface UnpackFoodDiaryEntryError {
    data object EntryNotFoundFood : UnpackFoodDiaryEntryError

    data object MealNotFound : UnpackFoodDiaryEntryError

    data object EntryCannotBeUnpackedFood : UnpackFoodDiaryEntryError
}

class UnpackFoodDiaryEntryUseCase(
    private val entryRepository: FoodDiaryEntryRepository,
    private val mealRepository: MealRepository,
    private val transactionProvider: TransactionProvider,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) {
    suspend fun unpack(
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
        const val TAG = "UnpackFoodDiaryEntryUseCase"
    }
}
