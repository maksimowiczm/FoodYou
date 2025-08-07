package com.maksimowiczm.foodyou.business.settings.application.query

import com.maksimowiczm.foodyou.business.settings.domain.Settings
import com.maksimowiczm.foodyou.business.settings.infrastructure.preferences.LocalSettingsDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.Query
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

data object ObserveSettingsQuery : Query

internal class ObserveSettingsQueryHandler(
    private val settingsDataSource: LocalSettingsDataSource
) : QueryHandler<ObserveSettingsQuery, Settings> {
    override val queryType: KClass<ObserveSettingsQuery>
        get() = ObserveSettingsQuery::class

    override fun handle(query: ObserveSettingsQuery): Flow<Settings> = settingsDataSource.observe()
}
