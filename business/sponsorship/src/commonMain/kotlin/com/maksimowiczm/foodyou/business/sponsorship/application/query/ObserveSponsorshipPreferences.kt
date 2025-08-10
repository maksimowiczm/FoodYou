package com.maksimowiczm.foodyou.business.sponsorship.application.query

import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorshipPreferences
import com.maksimowiczm.foodyou.business.sponsorship.infrastructure.preferences.SponsorshipPreferencesDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow

data object ObserveSponsorshipPreferencesQuery : Query<SponsorshipPreferences>

internal class ObserveSponsorshipPreferencesQueryHandler(
    private val sponsorshipPreferencesDataSource: SponsorshipPreferencesDataSource
) : QueryHandler<ObserveSponsorshipPreferencesQuery, SponsorshipPreferences> {
    override fun handle(query: ObserveSponsorshipPreferencesQuery): Flow<SponsorshipPreferences> =
        sponsorshipPreferencesDataSource.observe()
}
