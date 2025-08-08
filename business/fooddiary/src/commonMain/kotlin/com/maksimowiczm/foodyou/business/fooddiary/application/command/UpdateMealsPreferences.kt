package com.maksimowiczm.foodyou.business.fooddiary.application.command

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

data class UpdateMealsPreferencesCommand(val mealsPreferences: MealsPreferences) : Command

internal class UpdateMealsPreferencesCommandHandler(
    private val localMealsPreferences: LocalMealsPreferencesDataSource
) : CommandHandler<UpdateMealsPreferencesCommand, Unit, Unit> {
    override val commandType: KClass<UpdateMealsPreferencesCommand>
        get() = UpdateMealsPreferencesCommand::class

    override suspend fun handle(command: UpdateMealsPreferencesCommand): Result<Unit, Unit> {
        localMealsPreferences.update(command.mealsPreferences)
        return Ok(Unit)
    }
}
