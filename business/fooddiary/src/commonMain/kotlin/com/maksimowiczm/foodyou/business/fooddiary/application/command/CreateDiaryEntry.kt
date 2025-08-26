package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntry
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFood
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.event.EventBus
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.date.DateProvider
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.logAndReturnFailure
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.business.shared.domain.fooddiary.FoodDiaryEntryCreatedDomainEvent
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.domain.food.FoodId
import com.maksimowiczm.foodyou.shared.common.domain.measurement.Measurement
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

data class CreateDiaryEntryCommand(
    val foodId: FoodId,
    val measurement: Measurement,
    val mealId: Long,
    val date: LocalDate,
    val food: DiaryFood,
) : Command<Long, CreateDiaryEntryError>

sealed interface CreateDiaryEntryError {
    data object MealNotFound : CreateDiaryEntryError

    data object InvalidMeasurement : CreateDiaryEntryError
}

internal class CreateDiaryEntryCommandHandler(
    private val diaryEntryDataSource: LocalDiaryEntryDataSource,
    private val mealDataSource: LocalMealDataSource,
    private val eventBus: EventBus,
    private val transactionProvider: DatabaseTransactionProvider,
    private val dateProvider: DateProvider,
) : CommandHandler<CreateDiaryEntryCommand, Long, CreateDiaryEntryError> {

    override suspend fun handle(
        command: CreateDiaryEntryCommand
    ): Result<Long, CreateDiaryEntryError> {

        when (command.measurement) {
            is Measurement.Gram,
            is Measurement.Ounce ->
                if (command.food.isLiquid) {
                    return FoodYouLogger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Food must not be liquid for gram measurement" },
                    )
                }

            is Measurement.Milliliter,
            is Measurement.FluidOunce ->
                if (!command.food.isLiquid) {
                    return FoodYouLogger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Food must be liquid for milliliter measurement" },
                    )
                }

            is Measurement.Package ->
                if (command.food.totalWeight == null) {
                    return FoodYouLogger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Total weight must be provided for package measurement" },
                    )
                }

            is Measurement.Serving ->
                if (command.food.servingWeight == null) {
                    return FoodYouLogger.logAndReturnFailure(
                        tag = TAG,
                        throwable = null,
                        error = CreateDiaryEntryError.InvalidMeasurement,
                        message = { "Total weight must be provided for serving measurement" },
                    )
                }
        }

        val mealId = command.mealId
        val meal = mealDataSource.observeMealById(mealId).firstOrNull()

        if (meal == null) {
            return FoodYouLogger.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = CreateDiaryEntryError.MealNotFound,
                message = { "Meal with ID $mealId not found" },
            )
        }

        val now = dateProvider.now()
        val entry = command.toDiaryEntry(now)
        val id = transactionProvider.withTransaction { diaryEntryDataSource.insert(entry) }
        eventBus.publish(
            FoodDiaryEntryCreatedDomainEvent(
                foodId = command.foodId,
                entryId = id,
                date = now,
                measurement = command.measurement,
            )
        )

        return Ok(id)
    }

    private companion object {
        private const val TAG = "CreateDiaryEntryCommandHandler"
    }
}

private fun CreateDiaryEntryCommand.toDiaryEntry(now: LocalDateTime): DiaryEntry =
    DiaryEntry(
        id = 0, // ID will be generated by the data source
        mealId = mealId,
        date = date,
        measurement = measurement,
        food = food,
        createdAt = now,
        updatedAt = now,
    )
