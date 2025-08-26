package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate

data class UpdateDiaryEntryCommand(
    val id: Long,
    val measurement: Measurement,
    val mealId: Long,
    val date: LocalDate,
) : Command<Unit, UpdateDiaryEntryError>

sealed interface UpdateDiaryEntryError {
    data object EntryNotFound : UpdateDiaryEntryError

    data object InvalidMeasurement : UpdateDiaryEntryError

    data object MealNotFound : UpdateDiaryEntryError
}

internal class UpdateDiaryEntryCommandHandler(
    private val localEntry: LocalDiaryEntryDataSource,
    private val mealDataSource: LocalMealDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
    private val dateProvider: DateProvider,
) : CommandHandler<UpdateDiaryEntryCommand, Unit, UpdateDiaryEntryError> {

    override suspend fun handle(
        command: UpdateDiaryEntryCommand
    ): Result<Unit, UpdateDiaryEntryError> =
        transactionProvider.withTransaction {
            val entry = localEntry.observeEntry(command.id).first()

            if (entry == null) {
                return@withTransaction FoodYouLogger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateDiaryEntryError.EntryNotFound,
                    message = { "Diary entry with id ${command.id} not found" },
                )
            }

            when (command.measurement) {
                is Measurement.Gram,
                is Measurement.Ounce ->
                    if (entry.food.isLiquid) {
                        return@withTransaction FoodYouLogger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Food must not be liquid for gram measurement" },
                        )
                    }

                is Measurement.Milliliter,
                is Measurement.FluidOunce ->
                    if (!entry.food.isLiquid) {
                        return@withTransaction FoodYouLogger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Food must be liquid for milliliter measurement" },
                        )
                    }

                is Measurement.Package ->
                    if (entry.food.totalWeight == null) {
                        return@withTransaction FoodYouLogger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Total weight must be provided for package measurement" },
                        )
                    }

                is Measurement.Serving ->
                    if (entry.food.servingWeight == null) {
                        return@withTransaction FoodYouLogger.logAndReturnFailure(
                            tag = TAG,
                            throwable = null,
                            error = UpdateDiaryEntryError.InvalidMeasurement,
                            message = { "Total weight must be provided for serving measurement" },
                        )
                    }
            }

            val mealId = command.mealId
            val meal = mealDataSource.observeMealById(mealId).firstOrNull()

            if (meal == null) {
                return@withTransaction FoodYouLogger.logAndReturnFailure(
                    tag = TAG,
                    throwable = null,
                    error = UpdateDiaryEntryError.MealNotFound,
                    message = { "Meal with ID $mealId not found" },
                )
            }

            val updated =
                entry.copy(
                    measurement = command.measurement,
                    mealId = mealId,
                    date = command.date,
                    updatedAt = dateProvider.now(),
                )

            localEntry.update(updated)
            Ok(Unit)
        }

    private companion object {
        const val TAG = "UpdateDiaryEntryCommandHandler"
    }
}
