package com.maksimowiczm.foodyou.account.application

import com.maksimowiczm.foodyou.account.domain.AccountRepository
import com.maksimowiczm.foodyou.account.domain.Settings
import com.maksimowiczm.foodyou.common.Err
import com.maksimowiczm.foodyou.common.LocalAccountId
import com.maksimowiczm.foodyou.common.Ok
import com.maksimowiczm.foodyou.common.Result

// This is sketchy command, because it uses a transform function as a parameter. This is okay if we
// do it all on device, but if we ever want to expose this over the network, it must be changed.
data class UpdateSettingsCommand(
    val localAccountId: LocalAccountId,
    val transform: (currentSettings: Settings) -> Settings,
) {
    sealed interface Error {
        data object AccountNotFound : Error
    }
}

class UpdateSettingsCommandHandler(private val accountRepository: AccountRepository) {
    suspend fun handle(command: UpdateSettingsCommand): Result<Unit, UpdateSettingsCommand.Error> {
        val account =
            accountRepository.load(command.localAccountId)
                ?: return Err(UpdateSettingsCommand.Error.AccountNotFound)

        account.updateSettings(command.transform)

        accountRepository.save(account)

        return Ok()
    }
}
