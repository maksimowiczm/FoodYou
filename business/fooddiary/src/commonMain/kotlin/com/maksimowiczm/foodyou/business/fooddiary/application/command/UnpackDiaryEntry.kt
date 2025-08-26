package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate

data class UnpackDiaryEntryCommand(
    val id: Long,
    val measurement: Measurement,
    val mealId: Long,
    val date: LocalDate,
) : Command<Unit, UnpackDiaryEntryError>

sealed interface UnpackDiaryEntryError {
    data object EntryNotFound : UnpackDiaryEntryError

    data object MealNotFound : UnpackDiaryEntryError

    data object EntryCannotBeUnpacked : UnpackDiaryEntryError
}

internal class UnpackDiaryEntryCommandHandler(
    private val localEntry: LocalDiaryEntryDataSource,
    private val mealDataSource: LocalMealDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
    private val dateProvider: DateProvider,
) : CommandHandler<UnpackDiaryEntryCommand, Unit, UnpackDiaryEntryError> {
    override suspend fun handle(
        command: UnpackDiaryEntryCommand
    ): Result<Unit, UnpackDiaryEntryError> =
        transactionProvider.withTransaction {
            val entry = localEntry.observeEntry(command.id).first()
            if (entry == null) {
                return@withTransaction ErrorLoggingUtils.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackDiaryEntryError.EntryNotFound,
                    message = { "Diary entry with id ${command.id} not found" },
                )
            }

            val food = entry.food
            if (food !is DiaryFoodRecipe) {
                return@withTransaction ErrorLoggingUtils.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackDiaryEntryError.EntryCannotBeUnpacked,
                    message = { "Diary entry with id ${command.id} cannot be unpacked" },
                )
            }

            val meal = mealDataSource.observeMealById(command.mealId).firstOrNull()
            if (meal == null) {
                return@withTransaction ErrorLoggingUtils.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UnpackDiaryEntryError.MealNotFound,
                    message = { "Meal with id ${command.mealId} not found" },
                )
            }

            // Replace the entry with unpacked entries

            localEntry.delete(entry)

            val now = dateProvider.observeDateTime().first()
            val unpacked = food.unpack(command.measurement)
            unpacked.forEach {
                val entry =
                    DiaryEntry(
                        id = 0,
                        mealId = command.mealId,
                        date = command.date,
                        measurement = it.measurement,
                        food = it.food,
                        createdAt = entry.createdAt,
                        updatedAt = now,
                    )

                localEntry.insert(entry)
            }

            return@withTransaction Ok(Unit)
        }

    private companion object {
        const val TAG = "UnpackDiaryEntryCommandHandler"
    }
}
