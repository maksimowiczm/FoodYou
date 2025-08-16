package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.domain.HomeCard
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Ok
import com.maksimowiczm.foodyou.shared.common.domain.result.Result

/**
 * Command to partially update user settings. Null values are ignored, meaning that only the
 * provided fields will be updated.
 */
data class PartialSettingsUpdateCommand(
    val lastRememberedVersion: String? = null,
    val hidePreviewDialog: Boolean? = null,
    val showTranslationWarning: Boolean? = null,
    val nutrientsOrder: List<NutrientsOrder>? = null,
    val secureScreen: Boolean? = null,
    val homeCardOrder: List<HomeCard>? = null,
    val expandGoalCard: Boolean? = null,
    val onboardingFinished: Boolean? = null,
) : Command<Unit, Unit>

internal class PartialSettingsUpdateCommandHandler(
    private val localSettings: LocalSettingsDataSource
) : CommandHandler<PartialSettingsUpdateCommand, Unit, Unit> {
    override suspend fun handle(command: PartialSettingsUpdateCommand): Result<Unit, Unit> {
        command.lastRememberedVersion?.let { localSettings.updateLastRememberedVersion(it) }
        command.hidePreviewDialog?.let { localSettings.updateHidePreviewDialog(it) }
        command.showTranslationWarning?.let { localSettings.updateShowTranslationWarning(it) }
        command.nutrientsOrder?.let { localSettings.updateNutrientsOrder(it) }
        command.secureScreen?.let { localSettings.updateSecureScreen(it) }
        command.homeCardOrder?.let { localSettings.updateHomeCardOrder(it) }
        command.expandGoalCard?.let { localSettings.updateExpandGoalCard(it) }
        command.onboardingFinished?.let { localSettings.updateOnboardingFinished(it) }
        return Ok(Unit)
    }
}
