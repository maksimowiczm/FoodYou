package com.maksimowiczm.foodyou.business.sponsorship.application.command

import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorshipPreferences
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.SponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.result.Ok
import com.maksimowiczm.foodyou.shared.common.result.Result

data object AllowRemoteSponsorshipsCommand : Command<Unit, Unit> {}

internal class AllowRemoteSponsorshipsCommandHandler(
    private val sponsorshipPreferencesDataSource: SponsorshipPreferencesDataSource
) : CommandHandler<AllowRemoteSponsorshipsCommand, Unit, Unit> {

    override suspend fun handle(command: AllowRemoteSponsorshipsCommand): Result<Unit, Unit> {
        sponsorshipPreferencesDataSource.update(SponsorshipPreferences(remoteAllowed = true))
        return Ok(Unit)
    }
}
