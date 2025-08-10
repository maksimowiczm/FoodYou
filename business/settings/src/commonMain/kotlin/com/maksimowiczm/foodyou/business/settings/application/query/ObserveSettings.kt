package com.maksimowiczm.foodyou.business.settings.application.query

import com.maksimowiczm.foodyou.business.settings.domain.HomeCard
import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data object ObserveSettingsQuery : Query<Settings>

internal class ObserveSettingsQueryHandler(
    private val settingsDataSource: LocalSettingsDataSource
) : QueryHandler<ObserveSettingsQuery, Settings> {

    override fun handle(query: ObserveSettingsQuery): Flow<Settings> =
        settingsDataSource.observe().map {
            it.copy(homeCardOrder = it.homeCardOrder.fillMissingCards())
        }
}

private fun List<HomeCard>.fillMissingCards(): List<HomeCard> {
    val missingCards = HomeCard.defaultOrder.filterNot { it in this }
    return this + missingCards
}
