package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

data class ReorderMealsCommand(val order: List<Long>) : Command<Unit, Unit>

internal class ReorderMealsCommandHandler(private val mealDataSource: LocalMealDataSource) :
    CommandHandler<ReorderMealsCommand, Unit, Unit> {

    override suspend fun handle(command: ReorderMealsCommand): Result<Unit, Unit> {
        mealDataSource.reorder(command.order)
        return Ok(Unit)
    }
}
