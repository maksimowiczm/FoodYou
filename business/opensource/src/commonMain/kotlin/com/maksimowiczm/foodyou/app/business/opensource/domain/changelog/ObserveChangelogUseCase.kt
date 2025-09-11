package com.maksimowiczm.foodyou.app.business.opensource.domain.changelog

import com.maksimowiczm.foodyou.app.business.opensource.domain.config.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ObserveChangelogUseCase(private val appConfig: AppConfig) {
    fun observe(): Flow<Changelog> = flowOf(StaticChangelog(appConfig))
}
