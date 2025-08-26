package com.maksimowiczm.foodyou.business.settings.application.command

import com.maksimowiczm.foodyou.business.settings.domain.languages
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.error.ErrorLoggingUtils
import com.maksimowiczm.foodyou.business.shared.application.infrastructure.system.SystemDetails
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

data class SetTranslationCommand(val tag: String?) : Command<Unit, SetTranslationError>

sealed interface SetTranslationError {
    data object TranslationNotFound : SetTranslationError
}

internal class SetTranslationCommandHandler(
    private val systemDetails: SystemDetails,
    private val settingsDataSource: LocalSettingsDataSource,
) : CommandHandler<SetTranslationCommand, Unit, SetTranslationError> {

    override suspend fun handle(command: SetTranslationCommand): Result<Unit, SetTranslationError> {

        if (command.tag == null) {
            systemDetails.setSystemLanguage()
            return Ok(Unit)
        }

        val translation = languages.firstOrNull { it.languageTag == command.tag }

        if (translation == null) {
            return ErrorLoggingUtils.logAndReturnFailure(
                tag = TAG,
                throwable = null,
                error = SetTranslationError.TranslationNotFound,
                message = {
                    "Translation with tag '${command.tag}' not found. Available translations: $languages"
                },
            )
        }

        systemDetails.setLanguage(translation.languageTag)
        settingsDataSource.updateShowTranslationWarning(true)

        return Ok(Unit)
    }

    private companion object {
        const val TAG = "SetTranslationCommandHandler"
    }
}
