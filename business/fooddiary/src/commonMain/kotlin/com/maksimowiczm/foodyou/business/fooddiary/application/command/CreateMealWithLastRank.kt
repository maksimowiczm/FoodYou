package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.persistence.DatabaseTransactionProvider
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result
import kotlinx.datetime.LocalTime

data class CreateMealWithLastRankCommand(val name: String, val from: LocalTime, val to: LocalTime) :
    Command<Unit, Unit>

internal class CreateMealWithLastRankCommandHandler(
    private val mealDataSource: LocalMealDataSource,
    private val transactionProvider: DatabaseTransactionProvider,
) : CommandHandler<CreateMealWithLastRankCommand, Unit, Unit> {

    override suspend fun handle(command: CreateMealWithLastRankCommand): Result<Unit, Unit> {
        transactionProvider.withTransaction { mealDataSource.insertWithLastRank(command.toMeal()) }
        return Ok(Unit)
    }
}

private fun CreateMealWithLastRankCommand.toMeal(): Meal =
    Meal(id = 0, name = this.name, from = this.from, to = this.to, rank = 0)
