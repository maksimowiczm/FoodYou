package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class ReorderMealsCommand(val order: List<Long>) : Command

internal class ReorderMealsCommandHandler(private val mealDataSource: LocalMealDataSource) :
    CommandHandler<ReorderMealsCommand, Unit, Unit> {

    override val commandType = ReorderMealsCommand::class

    override suspend fun handle(command: ReorderMealsCommand): Result<Unit, Unit> {
        mealDataSource.reorder(command.order)
        return Ok(Unit)
    }
}
