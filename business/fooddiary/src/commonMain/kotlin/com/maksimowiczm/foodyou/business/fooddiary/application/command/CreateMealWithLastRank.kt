package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.Meal
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass
import kotlinx.datetime.LocalTime

data class CreateMealWithLastRankCommand(val name: String, val from: LocalTime, val to: LocalTime) :
    Command

internal class CreateMealWithLastRankCommandHandler(
    private val mealDataSource: LocalMealDataSource
) : CommandHandler<CreateMealWithLastRankCommand, Unit, Unit> {
    override val commandType: KClass<CreateMealWithLastRankCommand>
        get() = CreateMealWithLastRankCommand::class

    override suspend fun handle(command: CreateMealWithLastRankCommand): Result<Unit, Unit> {
        mealDataSource.insertWithLastRank(command.toMeal())
        return Ok(Unit)
    }
}

private fun CreateMealWithLastRankCommand.toMeal(): Meal =
    Meal(id = 0, name = this.name, from = this.from, to = this.to, rank = 0)
