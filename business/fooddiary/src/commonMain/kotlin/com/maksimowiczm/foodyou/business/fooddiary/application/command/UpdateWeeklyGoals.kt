package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.WeeklyGoals
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalGoalsDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

data class UpdateWeeklyGoalsCommand(val weeklyGoals: WeeklyGoals) : Command<Unit, Unit>

internal class UpdateWeeklyGoalsCommandHandler(private val localGoals: LocalGoalsDataSource) :
    CommandHandler<UpdateWeeklyGoalsCommand, Unit, Unit> {

    override suspend fun handle(command: UpdateWeeklyGoalsCommand): Result<Unit, Unit> {
        localGoals.updateWeeklyGoals(command.weeklyGoals)
        return Ok(Unit)
    }
}
