package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

data class SetHidePreviewDialog(val hidePreviewDialog: Boolean) : Command<Unit, Unit>

internal class SetShowPreviewWarningCommandHandler(val localSettings: LocalSettingsDataSource) :
    CommandHandler<SetHidePreviewDialog, Unit, Unit> {
    override suspend fun handle(command: SetHidePreviewDialog): Result<Unit, Unit> {
        localSettings.updateHidePreviewDialog(command.hidePreviewDialog)
        return Ok(Unit)
    }
}
