package com.maksimowiczm.foodyou.business.settings.domain

import com.maksimowiczm.foodyou.business.shared.domain.config.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveChangelogUseCase(private val appConfig: AppConfig) {
    fun observe(): Flow<Changelog> = flowOf(StaticChangelog(appConfig))
}
