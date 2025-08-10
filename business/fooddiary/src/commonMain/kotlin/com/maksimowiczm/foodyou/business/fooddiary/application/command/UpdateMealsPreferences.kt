package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class UpdateMealsPreferencesCommand(val mealsPreferences: MealsPreferences) :
    Command<Unit, Unit>

internal class UpdateMealsPreferencesCommandHandler(
    private val localMealsPreferences: LocalMealsPreferencesDataSource
) : CommandHandler<UpdateMealsPreferencesCommand, Unit, Unit> {

    override suspend fun handle(command: UpdateMealsPreferencesCommand): Result<Unit, Unit> {
        localMealsPreferences.update(command.mealsPreferences)
        return Ok(Unit)
    }
}
